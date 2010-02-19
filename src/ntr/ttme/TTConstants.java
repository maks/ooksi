package ntr.ttme;

// TODO: add more languages
public interface TTConstants
{
    // type sizes
    public static final int SIZE_OF_BYTE = 1; // unsigned 8-bit
    public static final int SIZE_OF_CHAR = 1; // signed 8-bit
    public static final int SIZE_OF_USHORT = 2; // unsigned 16-bit
    public static final int SIZE_OF_SHORT = 2; // signed 16-bit
    public static final int SIZE_OF_ULONG = 4; // unsigned 32-bit
    public static final int SIZE_OF_LONG = 4; // signed 32-bit
    public static final int SIZE_OF_FIXED = 4;

    // platforms
    public static final int MIN_PLATFORM = 0;
    public static final int MAX_PLATFORM = 3;

    public static final short PLATFORM_ID_APPLEUNICODE = 0;
    public static final short PLATFORM_ID_MACINTOSH = 1;
    public static final short PLATFORM_ID_ISO = 2;
    public static final short PLATFORM_ID_MICROSOFT = 3;

    public static final String[] PLATFORM_NAMES =
        { "Apple Unicode",
          "Macintosh",
          "ISO",
          "Microsoft" };

    // Macintosh-specific platforms
    public static final int MIN_MAC_SPECIFIC = 0;
    public static final int MAX_MAC_SPECIFIC = 32;

    public static final short PLATFORM_SPECIFIC_MAC_ROMAN = 0;
    public static final short PLATFORM_SPECIFIC_MAC_JAPANESE = 1;
    public static final short PLATFORM_SPECIFIC_MAC_CHINESE = 2;
    public static final short PLATFORM_SPECIFIC_MAC_KOREAN = 3;
    public static final short PLATFORM_SPECIFIC_MAC_ARABIC = 4;
    public static final short PLATFORM_SPECIFIC_MAC_HEBREW = 5;
    public static final short PLATFORM_SPECIFIC_MAC_GREEK = 6;
    public static final short PLATFORM_SPECIFIC_MAC_RUSSIAN = 7;
    public static final short PLATFORM_SPECIFIC_MAC_RSYMBOL = 8;
    public static final short PLATFORM_SPECIFIC_MAC_DEVANAGARI = 9;
    public static final short PLATFORM_SPECIFIC_MAC_GURMUKHI = 10;
    public static final short PLATFORM_SPECIFIC_MAC_GUJARATI = 11;
    public static final short PLATFORM_SPECIFIC_MAC_ORIYA = 12;
    public static final short PLATFORM_SPECIFIC_MAC_BENGALI = 13;
    public static final short PLATFORM_SPECIFIC_MAC_TAMIL = 14;
    public static final short PLATFORM_SPECIFIC_MAC_TELUGU = 15;
    public static final short PLATFORM_SPECIFIC_MAC_KANNADA = 16;
    public static final short PLATFORM_SPECIFIC_MAC_MALAYALAM = 17;
    public static final short PLATFORM_SPECIFIC_MAC_SINHALESE = 18;
    public static final short PLATFORM_SPECIFIC_MAC_BURMESE = 19;
    public static final short PLATFORM_SPECIFIC_MAC_KHMER = 20;
    public static final short PLATFORM_SPECIFIC_MAC_THAI = 21;
    public static final short PLATFORM_SPECIFIC_MAC_LAOTIAN = 22;
    public static final short PLATFORM_SPECIFIC_MAC_GEORGIAN = 23;
    public static final short PLATFORM_SPECIFIC_MAC_ARMENIAN = 24;
    public static final short PLATFORM_SPECIFIC_MAC_MALDIVIAN = 25;
    public static final short PLATFORM_SPECIFIC_MAC_TIBETIAN = 26;
    public static final short PLATFORM_SPECIFIC_MAC_MONGOLIAN = 27;
    public static final short PLATFORM_SPECIFIC_MAC_GEEZ = 28;
    public static final short PLATFORM_SPECIFIC_MAC_SLAVIC = 29;
    public static final short PLATFORM_SPECIFIC_MAC_VIETNAMESE = 30;
    public static final short PLATFORM_SPECIFIC_MAC_SINDHI = 31;
    public static final short PLATFORM_SPECIFIC_MAC_UNINTERP = 32;

    public static final String[] MAC_SPECIFIC_NAMES =
        { "Roman",
          "Japanese",
          "Chinese",
          "Korean",
          "Arabic",
          "Hebrew",
          "Greek",
          "Russian",
          "RSymbol",
          "Devanagari",
          "Gurmukhi",
          "Gujarati",
          "Oriya",
          "Bengali",
          "Tamil",
          "Telugu",
          "Kannada",
          "Malayalam",
          "Sinhalese",
          "Burmese",
          "Khmer",
          "Thai",
          "Laotian",
          "Georgian",
          "Armenian",
          "Maldivian",
          "Tibetian",
          "Mongolian",
          "Geez",
          "Slavic",
          "Vietnamese",
          "Sindhi",
          "Uninterp" };

    // microsoft-specific platforms
    public static final int MIN_MS_SPECIFIC = 0;
    public static final int MAX_MS_SPECIFIC = 1;

    public static final short PLATFORM_SPECIFIC_MS_UNDEFINED = 0;
    public static final short PLATFORM_SPECIFIC_MS_UGL = 1;

    public static final String[] MS_SPECIFIC_NAMES =
        { "Undefined",
          "UGL" };

    // ISO-specific platforms
    public static final int MIN_ISO_SPECIFIC = 0;
    public static final int MAX_ISO_SPECIFIC = 2;

    public static final short PLATFORM_SPECIFIC_ISO_ASCII = 0;
    public static final short PLATFORM_SPECIFIC_ISO_10646 = 1;
    public static final short PLATFORM_SPECIFIC_ISO_8859_1 = 2;

    public static final String[] ISO_SPECIFIC_NAMES =
        { "ISO-ASCII",
          "ISO-10646",
          "ISO-8859 1" };

    // microsoft language-identifiers
    public static final short LANGUAGE_ID_MS_ARABIC = 0x0401;
    public static final short LANGUAGE_ID_MS_BULGARIAN = 0x0402;
    public static final short LANGUAGE_ID_MS_CATALAN = 0x0403;
    public static final short LANGUAGE_ID_MS_TRADITIONALCHINESE = 0x0404;
    public static final short LANGUAGE_ID_MS_SIMPLIFIEDCHINESE = 0x0804;
    public static final short LANGUAGE_ID_MS_CZECH = 0x0405;
    public static final short LANGUAGE_ID_MS_DANISH = 0x0406;
    public static final short LANGUAGE_ID_MS_GERMAN = 0x0407;
    public static final short LANGUAGE_ID_MS_SWISSGERMAN = 0x0807;
    public static final short LANGUAGE_ID_MS_GREEK = 0x0408;
    public static final short LANGUAGE_ID_MS_USENGLISH = 0x0409;
    public static final short LANGUAGE_ID_MS_UKENGLISH = 0x0809;
    public static final short LANGUAGE_ID_MS_CASTILIANSPANISH = 0x040A;
    public static final short LANGUAGE_ID_MS_MEXICANSPANISH = 0x080A;
    public static final short LANGUAGE_ID_MS_MODERNSPANISH = 0x0C0A;
    public static final short LANGUAGE_ID_MS_FINNISH = 0x040B;
    public static final short LANGUAGE_ID_MS_FRENCH = 0x040C;
    public static final short LANGUAGE_ID_MS_BELGIANFRENCH = 0x080C;
    public static final short LANGUAGE_ID_MS_CANADIANFRENCH = 0x0C0C;
    public static final short LANGUAGE_ID_MS_SWISSFRENCH = 0x100C;
    public static final short LANGUAGE_ID_MS_HEBREW = 0x040D;
    public static final short LANGUAGE_ID_MS_HUNGARIAN = 0x040E;
    public static final short LANGUAGE_ID_MS_ICELANDIC = 0x040F;
    public static final short LANGUAGE_ID_MS_ITALIAN = 0x0410;
    public static final short LANGUAGE_ID_MS_SWISSITALIAN = 0x0810;
    public static final short LANGUAGE_ID_MS_JAPANESE = 0x0411;
    public static final short LANGUAGE_ID_MS_KOREAN = 0x0412;
    public static final short LANGUAGE_ID_MS_DUTCH = 0x0413;
    public static final short LANGUAGE_ID_MS_BELGIANDUTCH = 0x0813;
    public static final short LANGUAGE_ID_MS_NORWEGIANBOKMAL = 0x0414;
    public static final short LANGUAGE_ID_MS_NORWEGIANNYNORSK = 0x0814;
    public static final short LANGUAGE_ID_MS_POLISH = 0x0415;
    public static final short LANGUAGE_ID_MS_BRAZILIANPORTUGUESE = 0x0416;
    public static final short LANGUAGE_ID_MS_PORTUGUESE = 0x0816;
    public static final short LANGUAGE_ID_MS_RHAETOROMANIC = 0x0417;
    public static final short LANGUAGE_ID_MS_ROMANIAN = 0x0418;
    public static final short LANGUAGE_ID_MS_RUSSIAN = 0x0419;
    public static final short LANGUAGE_ID_MS_CROATOSERBIAN = 0x041A;
    public static final short LANGUAGE_ID_MS_SERBOCROATIAN = 0x081A;
    public static final short LANGUAGE_ID_MS_SLOVAKIAN = 0x041B;
    public static final short LANGUAGE_ID_MS_ALBANIAN = 0x041C;
    public static final short LANGUAGE_ID_MS_SWEDISH = 0x041D;
    public static final short LANGUAGE_ID_MS_THAI = 0x041E;
    public static final short LANGUAGE_ID_MS_TURKISH = 0x041F;
    public static final short LANGUAGE_ID_MS_URDU = 0x0420;
    public static final short LANGUAGE_ID_MS_BAHASA = 0x0421;
    public static final short LANGUAGE_ID_MS_AMHARIC = 0x045E;
    public static final short LANGUAGE_ID_MS_TELUGU = 0x044A;

    // macintosh language-identifiers
    public static final short LANGUAGE_ID_MAC_ENGLISH = 0;
    public static final short LANGUAGE_ID_MAC_FRENCH = 1;
    public static final short LANGUAGE_ID_MAC_GERMAN = 2;
    public static final short LANGUAGE_ID_MAC_ITALIAN = 3;
    public static final short LANGUAGE_ID_MAC_DUTCH = 4;
    public static final short LANGUAGE_ID_MAC_SWEDISH = 5;
    public static final short LANGUAGE_ID_MAC_SPANISH = 6;
    public static final short LANGUAGE_ID_MAC_DANISH = 7;
    public static final short LANGUAGE_ID_MAC_PORTUGUESE = 8;
    public static final short LANGUAGE_ID_MAC_NORWEGIAN = 9;
    public static final short LANGUAGE_ID_MAC_HEBREW = 10;
    public static final short LANGUAGE_ID_MAC_JAPANESE = 11;
    public static final short LANGUAGE_ID_MAC_ARABIC = 12;
    public static final short LANGUAGE_ID_MAC_FINNISH = 13;
    public static final short LANGUAGE_ID_MAC_GREEK = 14;
    public static final short LANGUAGE_ID_MAC_ICELANDIC = 15;
    public static final short LANGUAGE_ID_MAC_MALTESE = 16;
    public static final short LANGUAGE_ID_MAC_TURKISH = 17;
    public static final short LANGUAGE_ID_MAC_YUGOSLAVIAN = 18;
    public static final short LANGUAGE_ID_MAC_CHINESE = 19;
    public static final short LANGUAGE_ID_MAC_URDU = 20;
    public static final short LANGUAGE_ID_MAC_HINDI = 21;
    public static final short LANGUAGE_ID_MAC_THAI = 22;
    public static final short LANGUAGE_ID_MAC_AMHARIC = 85;
    public static final short LANGUAGE_ID_MAC_TELUGU = 75;

    // name-identifiers
    public static final short NAME_ID_COPYRIGHT = 0;
    public static final short NAME_ID_FAMILY = 1;
    public static final short NAME_ID_SUBFAMILY = 2;
    public static final short NAME_ID_UNIQUEID = 3;
    public static final short NAME_ID_FULLNAME = 4;
    public static final short NAME_ID_VERSION = 5;
    public static final short NAME_ID_POSTSCRIPTNAME = 6;
    public static final short NAME_ID_TRADEMARK = 7;

    // CMAP table-format IDs
    public static final short CMAP_FORMAT0 = 0;
    public static final short CMAP_FORMAT4 = 4;
    public static final short CMAP_FORMAT6 = 6;

    // misc
    public static final int OFFSET_TABLE_SIZE = 12;

} // END TTConstants
