package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.WordUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.sblendorio.bbs.core.Colors.*;
import static eu.sblendorio.bbs.core.Keys.*;
import static eu.sblendorio.bbs.core.Utils.*;
import static java.util.Arrays.asList;
import static eu.sblendorio.bbs.core.Utils.filterPrintable;
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TheOldNetBrowserV3 extends PetsciiThread {

    public static final String URL_TEMPLATE = "http://theoldnet.com/get?url=";

    protected int __currentPage = 1;
    protected int __pageSize = 10;
    protected int __screenRows = 16;

    static class Entry {
        public final String name;
        public final String url;
        public final String fileType;

        public Entry(String url, String name) throws Exception {
            this.url = defaultString(url);
            this.name = name;
	    //if (name.length() > 60){
            //	this.name = "..." + StringUtils.right(name, 32).trim();
	    //} else {
            //	this.name = StringUtils.left(name, 35).trim();
	    //}
            this.fileType = defaultString(this.name).replaceAll("(?is)^.*\\.(.*?)$", "$1").toLowerCase();
        }
    }

    protected Map<Integer, Entry> posts = emptyMap();

    public static void main(String[] args) throws Exception {}

    @Override
    public void doLoop() throws Exception {
        do {
            renderHomeScreen();
            resetInput();

            String search = readLine();
            
            if (defaultString(search).trim().equals(".") || isBlank(search)) {
                return;
            }

            String url = URL_TEMPLATE + URLEncoder.encode(search, "UTF-8");

            println();
            println();

            waitOn();
            
            Document webpage = getWebpage(url);
            displayPage(webpage, url);
            
        } while (true);
    }

    void renderHomeScreen() throws Exception {
            logo();
            // write(BROWSERSPLASH);
            // println();
            gotoXY(10,1);
            // print("URL: ");
            // write(GREY1);
            // println("(\".\" to go back):");
            // write(GREY3);
            // println();
            // gotoXY(10,2);
            // print(StringUtils.repeat(chr(163), 21));
            // write(UP, UP);
            // gotoXY(15,1);
            flush();
    }

    protected void displayPage(Document webpage, String url) throws Exception {
        __currentPage = 1;

        // cls();
        // logo();

        String title = url;

        String pageAsString = webpage.toString();

        final String content = pageAsString
            .replaceAll("<img.[^>]*>", "<br>[IMAGE] ")
            .replaceAll("<a.[^>]*>", " <br>[LINK] ")
            .replaceAll("&quot;", "\"")
            .replaceAll("&apos;", "'")
            .replaceAll("&#xA0;", " ")
            .replaceAll("(?is)<style>.*</style>", EMPTY)
            .replaceAll("(?is)<script .*</script>", EMPTY)
            .replaceAll("(?is)^[\\s\\n\\r]+|^\\s*(</?(br|div|figure|iframe|img|p|h[0-9])[^>]*>\\s*)+", EMPTY)
            .replaceAll("(?is)^(<[^>]+>(\\s|\n|\r)*)+", EMPTY);


        String head;
        try {
            head = url.split("url=")[1];
        } catch (ArrayIndexOutOfBoundsException e){
            head = url;
        }

        writeAddressBar(head);
        writeFooter();

        List<String> rows = wordWrap("");
        rows.addAll(wordWrap(content));


        int page = 1;
        int currentRow = 0;
        boolean forward = true;

        while (currentRow < rows.size() + 1) {
            log("Current Row: " + Integer.toString(currentRow));
            log("Rows: " + Integer.toString(rows.size()));
            log("Page: " + Integer.toString(page));
            log("Prior Page Start Row: " + Integer.toString((page - 1 )* __screenRows));
            

            boolean startOfDocument = page <= 1;
            boolean endOfDocument = currentRow == rows.size();

            boolean startOfPage = currentRow % __screenRows == 1;
            boolean endOfPage = currentRow > 0 && currentRow % __screenRows == 0 && forward;

            // if (endOfDocument){
            //     println();
            //     write(RED);
            //     println("-- End of Document --");
            //     write(GREY3);
            // }

            if (startOfPage){
                printPageNumber(page);
            }

            if (endOfPage || endOfDocument) { 

                // println();
                // gotoXY(0,21);
                // write(BROWSERBOTTOM);
                // printPageNumber(page);
		write(BLACK);
                gotoXY(9,1);
                
                // print("PAGE " + page + " (N)EXT  (P)REV  (L)INKS (B)ACK");
                write(GREY3);

                resetInput(); 
                int ch = readKey();

                if (ch == '.' || ch == 'b' || ch == 'B') {
                    return; //should bail
                } else if (ch == 'l' || ch == 'L') {
                    // waitOn();
                    getAndDisplayLinksOnPage(webpage);
                    currentRow = 0;
                    page = 0;
                } else if ((ch == 'p' || ch == 'P')) {  //PREVIOUS PAGE
                    if (startOfDocument){
                        continue;
                    }

                    --page;
                    currentRow = ( page -1 ) * __screenRows;
                    forward = false;
                    prepareDisplayForNewPage(head);
                    continue;

                } else if (ch == 'n' || ch == 'N') {  //NEXT PAGE
                    if (endOfDocument){
                        continue;
                    }
                    ++page;
                    //shouldn't next page explicitly draw it just like previous page?
                    //It should also set forward to true instead of implying it.
                } else {
                    continue;
                }

                prepareDisplayForNewPage(head);
            }

            //success path
            if (!endOfDocument){

                String row = rows.get(currentRow);

                String patternStringLink = ".*\\[LINK\\].*";
                Pattern patternLink = Pattern.compile(patternStringLink);
                Matcher matcherLink = patternLink.matcher(row);
                boolean matchesLink = matcherLink.matches();

                String patternStringImage = ".*\\[IMAGE\\].*";
                Pattern patternImage = Pattern.compile(patternStringImage);
                Matcher matcherImage = patternImage.matcher(row);
                boolean matchesImage = matcherImage.matches();

                if (matchesLink){
                    log("MATCHES!!!!!!!!!!!");
                    write(LIGHT_BLUE);
                }

                if (matchesImage){
                    log("MATCHES!!!!!!!!!!!");
                    write(YELLOW);
                }
                gotoXY(0, currentRow % __screenRows + 3);
                print(row);
                
                if (matchesLink || matchesImage){
                    write(GREY3);
                }
                
                forward = true;
                ++currentRow;
            }

        }
    }

    void printPageNumber(int page){
        write(BLACK);
	gotoXY(1,22);
        write(WHITE);
        print("PAGE " + page);
        write(GREY3);
    }

    void prepareDisplayForNewPage(String head){
        // cls();
        // logo();
        waitOn();
        clearBrowserWindow();
        writeAddressBar(head);
    }

    void writeAddressBar(String url){
        write(GREEN);
        gotoXY(10,1);
        print(StringUtils.left(url, 28));
        // print ("[URL: " + StringUtils.left(head, 30) + "]");
        gotoXY(0,3);
        write(GREY3);
    }

    public void getAndDisplayLinksOnPage(Document webpage) throws Exception{
        // waitOn();
        List<Entry> entries = getUrls(webpage);
        // waitOff();
        if (isEmpty(entries)) {
            write(RED); println("Zero result page - press any key");
            flush(); 
            resetInput(); 
            readKey();
            return;
        }
        displayLinksOnPage(entries);
    }

    public void displayLinksOnPage(List<Entry> entries) throws Exception {
        listPosts(entries);
        while (true) {
            log("TheOldNet Browser waiting for input");
            write(WHITE);print("#"); write(GREY3);
            print(", [");
            write(WHITE); print("+-"); write(GREY3);
            print("]Page [");
            write(WHITE); print("H"); write(GREY3);
            print("]elp [");
            write(WHITE); print("R"); write(GREY3);
            print("]eload [");
            write(WHITE); print("B"); write(GREY3);
            print("]ack> ");
            resetInput();
            flush(); 
            String inputRaw = readLine();
            String input = lowerCase(trim(inputRaw));
            if ("B".equals(input) || "b".equals(input) || ".".equals(input) || "exit".equals(input) || "quit".equals(input) || "q".equals(input)) {
                break;
            } else if ("help".equals(input) || "h".equals(input)) {
                help();
                listPosts(entries);
            } else if ("+".equals(input)) {
                ++__currentPage;
                posts = null;
                try {
                    listPosts(entries);
                } catch (NullPointerException e) {
                    --__currentPage;
                    posts = null;
                    listPosts(entries);
                }
            } else if ("-".equals(input) && __currentPage > 1) {
                --__currentPage;
                posts = null;
                listPosts(entries);
            } else if ("--".equals(input) && __currentPage > 1) {
                __currentPage = 1;
                posts = null;
                listPosts(entries);
            } else if ("r".equals(input) || "reload".equals(input) || "refresh".equals(input)) {
                posts = null;
                listPosts(entries);
            } else if (posts.containsKey(toInt(input))) { //what condition is this?
                // displayPost(toInt(input));
                final Entry p = posts.get(toInt(input));

                // displayPage(p.url);
                // getAndDisplayLinksOnPage(p.url);
                log("I DO NOTHING");
                Document webpage = getWebpage(p.url);
                displayPage(webpage, p.url);
                listPosts(entries); //redraw after coming back?
                
                // listPosts(entries);
            } else if ("".equals(input)) {
                listPosts(entries);
            }
        }
        flush();
    }  

    private void listPosts(List<Entry> entries) throws Exception {
        // logo();
        clearBrowserWindow();
        gotoXY(0,4);
        write(ORANGE);
        println("Links On Page:");
        println();
        posts = getPosts(entries, __currentPage, __pageSize);
        for (Map.Entry<Integer, Entry> entry: posts.entrySet()) {
            int i = entry.getKey();
            Entry post = entry.getValue();
            write(WHITE); print(i + "."); write(GREY3);
            final int iLen = 37-String.valueOf(i).length();
            String title = post.name;
            String line = WordUtils.wrap(filterPrintable(HtmlUtils.htmlClean(title)), iLen, "\r", true);
            println(line.replaceAll("\r", "\r " + repeat(" ", 37-iLen)));
        }
        newline();
    }

    private Map<Integer, Entry> getPosts(List<Entry> entries, int page, int perPage) throws Exception {
        if (page < 1 || perPage < 1) return null;

        Map<Integer, Entry> result = new LinkedHashMap<>();
        for (int i=(page-1)*perPage; i<page*perPage; ++i)
            if (i<entries.size()) result.put(i+1, entries.get(i));
        return result;
    }

    public static List<Entry> getUrls(Document webpage) throws Exception {
        List<Entry> urls = new ArrayList<>(); //why
        String title = webpage.title();
        Elements links = webpage.select("a[href]");
        Element link;

        for(int j=0; j < links.size(); j++){
            link=links.get(j);

            String label = "Empty";
            if (!StringUtils.isBlank(link.text())){
                label = link.text();
            } else {
                try {
                    label = link.attr("href").split("url=")[1];
                } catch (ArrayIndexOutOfBoundsException e){
                    label = link.attr("href");
                }
            }
            
            urls.add(new Entry(link.attr("href"), label));

        }
        return urls;
    }

    public static Document getWebpage(String url) throws Exception {
        Document doc = null;
        try{    
            doc = Jsoup.connect(url).get();
        } 
        catch (Exception ex){     
            System.out.println("Couldn't connect with the website."); 
        }
        return doc;
    }

    protected List<String> wordWrap(String s) {
        String[] cleaned = filterPrintableWithNewline(HtmlUtils.htmlClean(s)).split("\n");
        List<String> result = new ArrayList<>();
        for (String item: cleaned) {
            String[] wrappedLine = WordUtils
                    .wrap(item, 39, "\n", true)
                    .split("\n");
            result.addAll(asList(wrappedLine));
        }
        return result;
    }  

    private void waitOn() {
        gotoXY(10,1);
        write(PURPLE);
        print("LOADING...                 ");
        write(BLACK);
        flush();
    }

    private void clearBrowserWindow(){
        write(BLACK);
            gotoXY(0, 3);
        for (int i=0; i<720; ++i) {
            //gotoXY(0, i + 3);
            //for (int j=0; j<40; ++j) {
                write(SPACE_CHAR);
            //}
        }
        flush();
        write(GREY3);
    }

    private void waitOff() {
        for (int i=0; i<10; ++i) {
            write(DEL);
        }
        flush();
    }

    private void help() throws Exception {
        logo();
        println();
        println();
        println("Press any key to go back");
        readKey();
    }

    private void logo() throws Exception {
        write(CLR, LOWERCASE, CASE_LOCK);
        // write(TheOldNet.LOGO);
        // write(LOGO);
        write(BROWSERTOP);
        write(GREY3); 
        gotoXY(0,5);
    }

    private void writeFooter() throws Exception {
        gotoXY(0,21);
        write(BROWSERBOTTOM);
    }

    private final static byte[] LOGO = {
        -102, 32, 18, 32, 30, 32, -104, -110, 32, 32, 18, 32, -102, -110, 32, -104,
        32, 32, 18, 32, -110, 32, 18, 32, -110, 32, 32, 32, 18, 32, -110, 32,
        18, 32, -110, 32, 32, 32, 18, 32, -110, 32, 32, 32, 32, 32, -102, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 18, 32, 30, 32,
        32, -102, 32, -104, -110, 32, 18, 32, -110, 32, 18, 32, -110, 32, 18, 32,
        -110, 32, 18, 32, -110, 32, 18, 32, -110, 32, 18, 32, -110, 32, 18, 32,
        -110, 32, 18, 32, -110, 32, 18, 32, -110, 32, 32, 32, -102, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 18, 32, 30, 32,
        -102, 32, 32, -104, -110, 32, 32, 18, 32, -110, 32, 18, 32, -110, 32, 32,
        32, 18, 32, -110, 32, 18, 32, -110, 32, 32, 32, 18, 32, -110, 32, 18,
        32, -110, 32, 32, 32, 32, -102, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, -104, 32, -102, 18, 32, 30, 32, -104, -110, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, -102, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, -104, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, -102, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, -104,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, -102, 32, -104, 32, 32, -102, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, -104, 32, 32, 32, 32, -102, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, -104, 32, -102,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32,
        13
    };

    private final static byte[] BROWSERTOP = {
        -101, 18, 32, 32, 32, 32, 32, 32, 32, 32, 32, -110, -73, -73, -73, -73,
        -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73,
        -73, -73, -73, -73, -73, -73, -73, -73, -73, 18, 32, 32, -104, 32, 32, 32,
        -43, -46, -52, 32, 32, 32, 31, -110, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 5, 32, 32, 32, 32, -104, 18, 32, 32, -105, 32, 32, 32, 32, 32,
        32, 32, 32, 32, -110, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81,
        -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81,
        -81, -81, 18, 32, 32, -102, -110, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,

        32, 32, 32, 32, 32, 32, 32,

        13
    };

    private final static byte[] BROWSERBOTTOM = {
        -101, 18, 32, -110, -73, -73, -73, -73, -73, -73, 18, 32, -110, -73, -73, -73,
        -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, 18, 32, -110, -73,
        -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, 18, 32,
        -104, 32, 5, -110, 32, 32, 32, 32, 32, 32, -104, 18, 32, 5, -110, 32,
        -104, 91, 5, 80, -104, 93, -101, 82, 69, 86, 5, 32, -104, 91, 5, 78,
        -104, 93, -101, 69, 88, 84, 32, -104, 18, 32, -110, 91, 5, 76, -104, 93,
        -101, 73, 78, 75, 83, 32, -104, 91, 5, 66, -104, 93, -101, 65, 67, 75,
        -102, 32, -104, 18, 32, -105, 32, -110, -81, -81, -81, -81, -81, -81, 18, 32,
        -110, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81,
        18, 32, -110, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81,
        -81, -81, 18, 32, -102, -110, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,

        32, 32, 32, 32, 32, 32
    };

    private final static byte[] BROWSERSPLASH = {
        -102, 32, 32, 32, 32, 32, 32, 18, 32, 32, 32, 32, 5, -110, -76, -102,
        18, 32, 32, 32, 5, -110, -76, -98, 18, 32, 32, 32, 5, -110, -76, -98,
        18, 32, 5, -110, -76, -102, 32, -98, 18, 32, 32, 5, -110, -76, -102, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, -94,
        18, 32, 30, 32, -102, -110, -94, 32, 32, 18, 32, 5, -110, -76, -102, 18,
        32, 5, -110, -76, -102, 18, 32, -110, 32, 32, 32, -98, 18, 32, 5, -110,
        -76, -98, 18, 32, 5, -110, -76, -98, 18, 32, 5, -110, -76, -102, 32, -98,
        18, 32, 5, -110, -76, -98, 18, 32, -102, -110, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 18, -95, 32, 30, 32, 32, -102,
        32, -110, -95, 32, 18, 32, 5, -110, -76, -102, 18, 32, 32, 32, -110, -72,
        32, 32, -98, 18, 32, 5, -110, -76, -98, 18, 32, 5, -110, -76, -98, 18,
        32, 5, -110, -76, -102, 32, -98, 18, 32, 5, -110, -76, -98, 18, 32, -102,
        -110, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        18, 32, 30, 32, 32, 32, 32, -102, 32, -110, 32, 18, 32, 5, -110, -76,
        -102, 18, 32, 5, -110, -76, -102, 18, 32, 32, 32, 5, -110, -76, -98, 18,
        32, 32, 32, 5, -110, -76, -98, 18, 32, 32, 5, -110, -76, -98, 18, 32,
        32, 5, -110, -76, -102, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 18, 32, 32, 30, 32, -102, 32, 32, 32, -110, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        18, -95, 32, 32, 30, 32, 32, -102, -110, -95, 32, 32, 28, 18, 32, 5,
        -110, -76, -102, 32, 28, 18, 32, 5, -110, -76, 28, 18, 32, 32, 32, 5,
        -110, -76, 28, 18, 32, 32, 32, 32, 5, -110, -76, -102, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 18,
        -94, 32, 30, 32, -102, -94, -110, 32, 32, 32, 28, 18, 32, 32, 5, -110,
        -76, 28, 18, 32, 5, -110, -76, 28, 18, 32, -102, -110, 32, 32, 32, 32,
        28, 18, 32, 5, -110, -76, -102, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 28, 18, 32, 5, -110, -76, 28, 18, 32, 32, 5, -110, -76,
        28, 18, 32, -110, -72, -102, 32, 32, 32, 28, 18, 32, 5, -110, -76, -102,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 28, 18, 32, 5,
        -110, -76, -102, 32, 28, 18, 32, 5, -110, -76, 28, 18, 32, 32, 32, 5,
        -110, -76, -102, 32, 28, 18, 32, 5, -110, -76, 30, 46, 67, 79, 77, -102,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,


        13
    };
}
