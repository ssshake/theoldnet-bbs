package eu.sblendorio.bbs.tenants.ascii;

import com.google.common.collect.ImmutableMap;
import eu.sblendorio.bbs.core.Hidden;
import static eu.sblendorio.bbs.core.Utils.bytes;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@Hidden
public class TelevideoRaiAscii extends RssAscii {

    protected Map<String, NewsSection> sections;
    protected String type;

    public TelevideoRaiAscii() {
        this("rss.a1.timeout", "40000", "ascii");
    }

    public TelevideoRaiAscii(String property, String defaultValue, String interfaceType) {
        super(property, defaultValue);
        type = interfaceType;
        sections = loadSections();
        timeout = toLong(System.getProperty(property, defaultValue));
        logoHeight = logoHeights.get(interfaceType);
    }

    @Override
    public byte[] getLogo() {
        return logo.get(type);
    }

    @Override
    public String prefix() {
        return "http://www.servizitelevideo.rai.it/televideo/pub/";
    }

    private Map<String, NewsSection> loadSections() {
        Map<String, NewsSection> result = new LinkedHashMap<>();
        result.put("101", new NewsSection("Ultim'Ora", prefix() + "rss101.xml", logos.get(type).get("101")));
        result.put("102", new NewsSection("24h No Stop", prefix() + "rss102.xml", logos.get(type).get("102")));
        result.put("110", new NewsSection("Primo Piano", prefix() + "rss110.xml", logos.get(type).get("110")));
        result.put("120", new NewsSection("Politica", prefix() + "rss120.xml", logos.get(type).get("120")));
        result.put("130", new NewsSection("Economia", prefix() + "rss130.xml", logos.get(type).get("130")));
        result.put("140", new NewsSection("Dall'Italia", prefix() + "rss140.xml", logos.get(type).get("140")));
        result.put("150", new NewsSection("Dal Mondo", prefix() + "rss150.xml", logos.get(type).get("150")));
        result.put("160", new NewsSection("Culture", prefix() + "rss160.xml", logos.get(type).get("160")));
        result.put("170", new NewsSection("Cittadini", prefix() + "rss170.xml", logos.get(type).get("170")));
        result.put("180", new NewsSection("Speciale", prefix() + "rss180.xml", logos.get(type).get("180")));
        result.put("190", new NewsSection("Atlante Crisi", prefix() + "rss190.xml", logos.get(type).get("190")));
        result.put("229", new NewsSection("Brevi Calcio", prefix() + "rss229.xml", logos.get(type).get("229")));
        result.put("230", new NewsSection("CalcioSquadre", prefix() + "rss230.xml", logos.get(type).get("230")));
        result.put("260", new NewsSection("Altri Sport", prefix() + "rss260.xml", logos.get(type).get("260")));
        result.put("299", new NewsSection("Brevissime", prefix() + "rss299.xml", logos.get(type).get("299")));
        result.put("810", new NewsSection("Motori", prefix() + "rss810.xml", logos.get(type).get("810")));
        return result;
    }

    @Override
    public Map<String, NewsSection> sections() {
        return sections;
    }

    public Map<String, byte[]> logo = ImmutableMap.of(
      "ascii", bytes("Televideo\r\n---------\r\n\r\n"),
      "ansi", readBinaryFile("ansi/Televideo.ans"),
      "utf8", readBinaryFile("ansi/Televideo.utf8ans")
    );

    public Map<String, Integer> logoHeights = ImmutableMap.of(
        "ascii", 2,
        "ansi", 4,
        "utf8", 4
    );

    public Map<String, Map<String, byte[]>> logos = ImmutableMap.of(
        "ascii", ImmutableMap.<String, byte[]> builder()
            .put("101", bytes("Televideo - Ultim'ora\r\n\r\n"))
            .put("102", bytes("Televideo - No stop - 24 ore\r\n\r\n"))
            .put("110", bytes("Televideo - Primo piano\r\n\r\n"))
            .put("120", bytes("Televideo - Politica\r\n\r\n"))
            .put("130", bytes("Televideo - Economia\r\n\r\n"))
            .put("140", bytes("Televideo - Dall'Italia\r\n\r\n"))
            .put("150", bytes("Televideo - Dal mondo\r\n\r\n"))
            .put("160", bytes("Televideo - Culture\r\n\r\n"))
            .put("170", bytes("Televideo - Cittadini\r\n\r\n"))
            .put("180", bytes("Televideo - Speciale\r\n\r\n"))
            .put("190", bytes("Televideo - Atlante crisi\r\n\r\n"))
            .put("229", bytes("Televideo - Brevi calcio\r\n\r\n"))
            .put("230", bytes("Televideo - Calcio - squadre\r\n\r\n"))
            .put("260", bytes("Televideo - Altri sport\r\n\r\n"))
            .put("299", bytes("Televideo - Sport - brevissime\r\n\r\n"))
            .put("810", bytes("Televideo - Motori\r\n\r\n"))
            .build(),
        "ansi", ImmutableMap.<String, byte[]> builder()
            .put("101", readBinaryFile("ansi/Ultimora.ans"))
            .put("102", readBinaryFile("ansi/24hNoStop.ans"))
            .put("110", readBinaryFile("ansi/PrimoPiano.ans"))
            .put("120", readBinaryFile("ansi/Politica.ans"))
            .put("130", readBinaryFile("ansi/Economia.ans"))
            .put("140", readBinaryFile("ansi/DallItalia.ans"))
            .put("150", readBinaryFile("ansi/DalMondo.ans"))
            .put("160", readBinaryFile("ansi/Culture.ans"))
            .put("170", readBinaryFile("ansi/Cittadini.ans"))
            .put("180", readBinaryFile("ansi/Speciale.ans"))
            .put("190", readBinaryFile("ansi/AtlanteCrisi.ans"))
            .put("229", readBinaryFile("ansi/BreviCalcio.ans"))
            .put("230", readBinaryFile("ansi/CalcioSquadre.ans"))
            .put("260", readBinaryFile("ansi/AltriSport.ans"))
            .put("299", readBinaryFile("ansi/Brevissime.ans"))
            .put("810", readBinaryFile("ansi/Motori.ans"))
            .build(),
        "utf8", ImmutableMap.<String, byte[]> builder()
            .put("101", readBinaryFile("ansi/Ultimora.utf8ans"))
            .put("102", readBinaryFile("ansi/24hNoStop.utf8ans"))
            .put("110", readBinaryFile("ansi/PrimoPiano.utf8ans"))
            .put("120", readBinaryFile("ansi/Politica.utf8ans"))
            .put("130", readBinaryFile("ansi/Economia.utf8ans"))
            .put("140", readBinaryFile("ansi/DallItalia.utf8ans"))
            .put("150", readBinaryFile("ansi/DalMondo.utf8ans"))
            .put("160", readBinaryFile("ansi/Culture.utf8ans"))
            .put("170", readBinaryFile("ansi/Cittadini.utf8ans"))
            .put("180", readBinaryFile("ansi/Speciale.utf8ans"))
            .put("190", readBinaryFile("ansi/AtlanteCrisi.utf8ans"))
            .put("229", readBinaryFile("ansi/BreviCalcio.utf8ans"))
            .put("230", readBinaryFile("ansi/CalcioSquadre.utf8ans"))
            .put("260", readBinaryFile("ansi/AltriSport.utf8ans"))
            .put("299", readBinaryFile("ansi/Brevissime.utf8ans"))
            .put("810", readBinaryFile("ansi/Motori.utf8ans"))
            .build()

    );
}
