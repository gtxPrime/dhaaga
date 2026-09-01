package com.dhaaga.app.utils

object AppLanguageManager {

    data class LanguageInfo(
        val code: String,
        val nativeName: String,
        val englishName: String
    )

    val SUPPORTED_LANGUAGES = listOf(
        LanguageInfo("en", "English", "English"),
        LanguageInfo("hi", "हिंदी", "Hindi"),
        LanguageInfo("bn", "বাংলা", "Bengali"),
        LanguageInfo("ta", "தமிழ்", "Tamil"),
        LanguageInfo("te", "తెలుగు", "Telugu"),
        LanguageInfo("mr", "मराठी", "Marathi"),
        LanguageInfo("gu", "ગુજરાતી", "Gujarati"),
        LanguageInfo("kn", "ಕನ್ನಡ", "Kannada"),
        LanguageInfo("ml", "മലയാളം", "Malayalam"),
        LanguageInfo("pa", "ਪੰਜਾਬੀ", "Punjabi"),
        LanguageInfo("or", "ଓଡ଼ିଆ", "Odia"),
        LanguageInfo("ur", "اردو", "Urdu"),
        LanguageInfo("as", "অসমীয়া", "Assamese"),
        LanguageInfo("sa", "संस्कृतम्", "Sanskrit"),
        LanguageInfo("doi", "डोगरी", "Dogri"),
        LanguageInfo("kok", "कोंकणी", "Konkani"),
        LanguageInfo("mai", "मैथिली", "Maithili"),
        LanguageInfo("ne", "नेपाली", "Nepali"),
        LanguageInfo("sat", "संताली", "Santali"),
        LanguageInfo("mni", "মৈতৈলোন্", "Manipuri"),
        LanguageInfo("ks", "کٲشُر", "Kashmiri"),
        LanguageInfo("bho", "भोजपुरी", "Bhojpuri")
    )

    private val translations: Map<String, Map<String, String>> = mapOf(
        // Navigation & General
        "home" to mapOf(
            "en" to "Home", "hi" to "होम", "bn" to "হোম", "ta" to "முகப்பு", "te" to "హోమ్",
            "mr" to "मुख्यपृष्ठ", "gu" to "હોમ", "kn" to "ಮುಖಪುಟ", "ml" to "ഹോം", "pa" to "ਮੁੱਖ",
            "or" to "ମୁଖ୍ୟ", "ur" to "ہوم", "as" to "গৃহ", "bho" to "घर", "mai" to "घर"
        ),
        "listings" to mapOf(
            "en" to "My Crafts", "hi" to "मेरी कृतियां", "bn" to "আমার শিল্প", "ta" to "என் கைவினை", "te" to "నా కళాఖండాలు",
            "mr" to "माझ्या कलाकृती", "gu" to "મારી કારીગરી", "kn" to "ನನ್ನ ಕರಕುಶಲ", "ml" to "എന്റെ ശില്പങ്ങൾ", "pa" to "ਮੇਰੀ ਕਲਾਕਾਰੀ",
            "or" to "ମୋର କଳା", "ur" to "میری دستکاری", "as" to "মোৰ শিল্প", "bho" to "हमार कलाकारी", "mai" to "हमर शिल्प"
        ),
        "dashboard" to mapOf(
            "en" to "Dashboard", "hi" to "डैशबोर्ड", "bn" to "ড্যাশবোর্ড", "ta" to "டாஷ்போர்டு", "te" to "డాష్‌బోర్డ్",
            "mr" to "डॅशबोर्ड", "gu" to "ડેશબોર્ડ", "kn" to "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್", "ml" to "ഡാഷ്‌ബോർഡ്", "pa" to "ਡੈਸ਼ਬੋਰਡ",
            "or" to "ଡ୍ୟାସବୋର୍ଡ", "ur" to "ڈیش بورڈ", "as" to "ডেচবৰ্ড", "bho" to "डैशबोर्ड", "mai" to "डैशबोर्ड"
        ),
        "profile" to mapOf(
            "en" to "Profile", "hi" to "प्रोफाइल", "bn" to "প্রোফাইল", "ta" to "சுயவிவரம்", "te" to "ప్రొఫైల్",
            "mr" to "प्रोफाइल", "gu" to "પ્રોફાઇલ", "kn" to "ಪ್ರೊಫೈಲ್", "ml" to "പ്രൊഫൈൽ", "pa" to "ਪ੍ਰੋਫਾਈਲ",
            "or" to "ପ୍ରୋଫାଇଲ", "ur" to "پروفائل", "as" to "প্ৰফাইল", "bho" to "प्रोफाइल", "mai" to "प्रोफाइल"
        ),
        "bag" to mapOf(
            "en" to "Shopping Bag", "hi" to "शॉपिंग बैग", "bn" to "শপিং ব্যাগ", "ta" to "ஷாப்பிங் பை", "te" to "షాపింగ్ బ్యాగ్",
            "mr" to "खरेदी पिशवी", "gu" to "શોપિંગ બેગ", "kn" to "ಶಾಪಿಂಗ್ ಬ್ಯಾಗ್", "ml" to "ഷോപ്പിംഗ് ബാഗ്", "pa" to "ਖਰੀਦਦਾਰੀ ਝੋਲਾ",
            "or" to "ସପିଂ ବ୍ୟାଗ୍", "ur" to "شاپنگ بیگ", "as" to "শ্বপিং বেগ", "bho" to "झोला", "mai" to "झोला"
        ),
        "wishlist" to mapOf(
            "en" to "Wishlist", "hi" to "पसंदीदा", "bn" to "পছন্দের তালিকা", "ta" to "விருப்பப் பட்டியல்", "te" to "ఇష్టమైనవి",
            "mr" to "आवडती यादी", "gu" to "પસંદીદા", "kn" to "ಇಷ್ಟಪಟ್ಟವು", "ml" to "ഇഷ്ടപ്പെട്ടവ", "pa" to "ਮਨਪਸੰਦ",
            "or" to "ପସନ୍ଦ ତାଲିକା", "ur" to "پسندیدہ", "as" to "পছন্দৰ তালিকা", "bho" to "पसंद", "mai" to "मनपसंद"
        ),
        "orders" to mapOf(
            "en" to "My Orders", "hi" to "मेरे ऑर्डर्स", "bn" to "আমার অর্ডার", "ta" to "என் ஆர்டர்கள்", "te" to "నా ఆర్డర్లు",
            "mr" to "माझ्या ऑर्डर्स", "gu" to "મારા ઓર્ડર", "kn" to "ನನ್ನ ಆದೇಶಗಳು", "ml" to "എന്റെ ഓർഡറുകൾ", "pa" to "ਮੇਰੇ ਆਰਡਰ",
            "or" to "ମୋର ଅର୍ଡର", "ur" to "میرے آرڈرز", "as" to "মোৰ অৰ্ডাৰ", "bho" to "हमार ऑर्डर", "mai" to "हमर ऑर्डर"
        ),
        "logout" to mapOf(
            "en" to "Log Out of Dhaaga", "hi" to "धागा से लॉग आउट करें", "bn" to "লগ আউট করুন", "ta" to "வெளியேறு", "te" to "లాగ్ అవుట్",
            "mr" to "लॉग आऊट करा", "gu" to "લૉગ આઉટ કરો", "kn" to "ಲಾಗ್ ಔಟ್", "ml" to "ലോഗ് ഔട്ട്", "pa" to "ਲਾਗ ਆਉਟ",
            "or" to "ଲଗ୍ ଆଉଟ୍", "ur" to "لاگ آؤٹ", "as" to "লগ আউট", "bho" to "लॉग आउट करीं", "mai" to "लॉग आउट करू"
        ),
        "language" to mapOf(
            "en" to "Language (भाषा)", "hi" to "भाषा (Language)", "bn" to "ভাষা", "ta" to "மொழி", "te" to "భాష",
            "mr" to "भाषा", "gu" to "ભાષા", "kn" to "ಭಾಷೆ", "ml" to "ഭാഷ", "pa" to "ਭਾਸ਼ਾ",
            "or" to "ଭାଷା", "ur" to "زبان", "as" to "ভাষা", "bho" to "भाषा", "mai" to "भाषा"
        ),

        // Header & Search
        "search_placeholder" to mapOf(
            "en" to "Search authentic Indian crafts...", "hi" to "प्रामाणिक भारतीय शिल्प खोजें...",
            "bn" to "প্রামাণিক ভারতীয় শিল্প অনুসন্ধান করুন...", "ta" to "பாரம்பரிய கைவினைகளைத் தேடுங்கள்...",
            "te" to "ప్రామాణిక భారతీయ కళాఖండాలను శోధించండి...", "mr" to "अस्सल भारतीय हस्तकला शोधा...",
            "gu" to "અસલ ભારતીય કારીગરી શોધો...", "kn" to "ಕರಕುಶಲ ವಸ್ತುಗಳನ್ನು ಹುಡುಕಿ...",
            "ml" to "ശില്പങ്ങൾ തിരയുക...", "pa" to "ਭਾਰਤੀ ਕਲਾਕ੍ਰਿਤੀਆਂ ਖੋਜੋ...",
            "or" to "ହସ୍ତତନ୍ତ ଖୋଜନ୍ତୁ...", "ur" to "دستکاری تلاش کریں...",
            "as" to "ভাৰতীয় শিল্প সন্ধান কৰক...", "bho" to "भारतीय कलाकारी खोजीं...", "mai" to "भारतीय शिल्प खोजू..."
        ),
        "artisan_label" to mapOf(
            "en" to "Artisan", "hi" to "कारीगर", "bn" to "কারিগর", "ta" to "கைவினைஞர்", "te" to "కళాకారుడు",
            "mr" to "कारागीर", "gu" to "કારીગર", "kn" to "ಕುಶಲಕರ್ಮಿ", "ml" to "ശില്പി", "pa" to "ਕਾਰੀਗਰ",
            "or" to "ଶିଳ୍ପୀ", "ur" to "دستکار", "as" to "শিল্পী", "bho" to "कारीगर", "mai" to "कारीगर"
        ),
        "craft_lover" to mapOf(
            "en" to "Craft Lover", "hi" to "कला प्रेमी", "bn" to "শিল্প অনুরাগী", "ta" to "கலை காதலர்", "te" to "కళా ప్రేమికుడు",
            "mr" to "कला प्रेमी", "gu" to "કલા પ્રેમી", "kn" to "ಕಲಾ ಪ್ರೇಮಿ", "ml" to "കലാ സ്നേഹി", "pa" to "ਕਲਾ ਪ੍ਰੇਮੀ",
            "or" to "କଳା ପ୍ରେମୀ", "ur" to "شائق دستکاری", "as" to "শিল্প প্ৰেমী", "bho" to "कला प्रेमी", "mai" to "कला प्रेमी"
        ),

        // Home Sections
        "trending_this_week" to mapOf(
            "en" to "TRENDING THIS WEEK", "hi" to "इस सप्ताह के लोकप्रिय शिल्प", "bn" to "এই সপ্তাহের জনপ্রিয় শিল্প",
            "ta" to "இந்த வாரத்தின் பிரபல கைவினைகள்", "te" to "ఈ వారం ట్రెండింగ్ కళలు", "mr" to "या आठवड्यातील लोकप्रिय हस्तकला",
            "gu" to "આ અઠવાડિયાની લોકપ્રિય કારીગરી", "kn" to "ಈ ವಾರದ ಜನಪ್ರಿಯ ಕರಕುಶಲ", "ml" to "ഈ ആഴ്ചയിലെ പ്രിയപ്പെട്ടവ",
            "pa" to "ਇਸ ਹਫ਼ਤੇ ਦੀ ਪ੍ਰਸਿੱਧ ਕਲਾ", "or" to "ଏହି ସପ୍ତାହର ଲୋକପ୍ରିୟ କଳା", "ur" to "اس ہفتے کے مقبول ترین فن پارے",
            "as" to "এই সপ্তাহৰ জনপ্ৰিয় শিল্প", "bho" to "एह हफ्ता के चर्चित शिल्प", "mai" to "एहि सप्ताहक लोकप्रिय शिल्प"
        ),
        "all_products" to mapOf(
            "en" to "ALL PRODUCTS", "hi" to "सभी उत्पाद", "bn" to "সমস্ত পণ্য", "ta" to "அனைத்து பொருட்கள்", "te" to "అన్ని ఉత్పత్తులు",
            "mr" to "सर्व उत्पादने", "gu" to "બધા ઉત્પાદનો", "kn" to "ಎಲ್ಲಾ ಉತ್ಪನ್ನಗಳು", "ml" to "എല്ലാ ഉൽപ്പന്നങ്ങളും", "pa" to "ਸਾਰੇ ਉਤਪਾਦ",
            "or" to "ସମସ୍ତ ଉତ୍ପାଦ", "ur" to "تمام مصنوعات", "as" to "সকলো সামগ্ৰী", "bho" to "सभ उत्पाद", "mai" to "सबहि उत्पाद"
        ),
        "items_suffix" to mapOf(
            "en" to "Items", "hi" to "उत्पाद", "bn" to "পণ্য", "ta" to "பொருட்கள்", "te" to "ఉత్పత్తులు",
            "mr" to "उत्पादने", "gu" to "વસ્તુઓ", "kn" to "ವಸ್ತುಗಳು", "ml" to "ഇനങ്ങൾ", "pa" to "ਚੀਜ਼ਾਂ",
            "or" to "ସାମଗ୍ରୀ", "ur" to "اشیاء", "as" to "বস্তু", "bho" to "समान", "mai" to "समान"
        ),
        "clear_filter" to mapOf(
            "en" to "Clear Filter", "hi" to "फ़िल्टर हटाएं", "bn" to "ফিল্টার মুছুন", "ta" to "வடிகட்டியை நீக்கு", "te" to "ఫిల్టర్ తొలగించు",
            "mr" to "फिल्टर काढा", "gu" to "ફિલ્ટર સાફ કરો", "kn" to "ಫಿಲ್ಟರ್ ತೆರವುಗೊಳಿಸಿ", "ml" to "ഫിൽട്ടർ മാറ്റുക", "pa" to "ਫਿਲਟਰ ਸਾਫ਼ ਕਰੋ",
            "or" to "ଫିଲ୍ଟର ହଟାନ୍ତୁ", "ur" to "فلٹر ختم کریں", "as" to "ফিল্টাৰ মচক", "bho" to "फ़िल्टर हटाईं", "mai" to "फ़िल्टर हटाउ"
        ),
        "no_crafts_found" to mapOf(
            "en" to "No crafts found", "hi" to "कोई शिल्प नहीं मिला", "bn" to "কোনো শিল্প খুঁজে পাওয়া যায়নি",
            "ta" to "கைவினைகள் எதுவும் கிடைக்கவில்லை", "te" to "కళాఖండాలు దొరకలేదు", "mr" to "हस्तकला सापडली नाही",
            "gu" to "કોઈ કારીગરી મળી નથી", "kn" to "ಯಾವುದೇ ಕರಕುಶಲ ವಸ್ತು ಸಿಗಲಿಲ್ಲ", "ml" to "ശില്പങ്ങൾ കണ്ടെത്തിയില്ല",
            "pa" to "ਕੋਈ ਕਲਾਕ੍ਰਿਤੀ ਨਹੀਂ ਮਿਲੀ", "or" to "କୌଣସି କଳା ମିଳିଲା ନାହିଁ", "ur" to "کوئی دستکاری نہیں ملی",
            "as" to "কোনো শিল্প পোৱা নগ'ল", "bho" to "कवनो शिल्प ना मिलल", "mai" to "कोनो शिल्प नहि भेटल"
        ),

        // Categories
        "cat_all" to mapOf(
            "en" to "All", "hi" to "सभी", "bn" to "সব", "ta" to "அனைத்தும்", "te" to "అన్నీ",
            "mr" to "सर्व", "gu" to "બધું", "kn" to "ಎಲ್ಲಾ", "ml" to "എല്ലാം", "pa" to "ਸਭ",
            "or" to "ସବୁ", "ur" to "تمام", "as" to "সকলো", "bho" to "सभ", "mai" to "सब"
        ),
        "cat_warli" to mapOf(
            "en" to "Warli Art", "hi" to "वारली कला", "bn" to "ওয়ার্লি শিল্প", "ta" to "வார்லி கலை", "te" to "వార్లి కళ",
            "mr" to "वारली कला", "gu" to "વારલી કલા", "kn" to "ವಾರ್ಲಿ ಕಲೆ", "ml" to "വാർലി കല", "pa" to "ਵਾਰਲੀ ਕਲਾ",
            "or" to "ୱାର୍ଲି କଳା", "ur" to "وارلی آرٹ", "as" to "ৱাৰ্লি শিল্প", "bho" to "वारली कला", "mai" to "वारली कला"
        ),
        "cat_madhubani" to mapOf(
            "en" to "Madhubani", "hi" to "मधुबनी", "bn" to "মধুবনী", "ta" to "மதுபானி", "te" to "మధుబని",
            "mr" to "मधुबनी", "gu" to "મધુબની", "kn" to "ಮಧುಬನಿ", "ml" to "മധുബനി", "pa" to "ਮਧੂਬਨੀ",
            "or" to "ମଧୁବନୀ", "ur" to "مدھوبنی", "as" to "মধুৱনী", "bho" to "मधुबनी", "mai" to "मधुबनी"
        ),
        "cat_handloom" to mapOf(
            "en" to "Handloom", "hi" to "हथकरघा", "bn" to "তাঁত", "ta" to "கைத்தறி", "te" to "చేనేత",
            "mr" to "हातमाग", "gu" to "હાથશાળ", "kn" to "ಕೈಮಗ್ಗ", "ml" to "കൈത്തറി", "pa" to "ਖੱਡੀ",
            "or" to "ହସ୍ତତନ୍ତ", "ur" to "ہینڈلوم", "as" to "তাঁতশাল", "bho" to "हथकरघा", "mai" to "हथकरघा"
        ),
        "cat_terracotta" to mapOf(
            "en" to "Terracotta", "hi" to "टेराकोटा", "bn" to "টেরাকোটা", "ta" to "சுடுமண்", "te" to "టెర్రకోట",
            "mr" to "टेराकोटा", "gu" to "ટેરાકોટા", "kn" to "ಟೆರಾಕೋಟಾ", "ml" to "ടെറാക്കോട്ട", "pa" to "ਟੈਰਾਕੋਟਾ",
            "or" to "ଟେରାକୋଟା", "ur" to "ٹیرাকوٹا", "as" to "টেৰাকোটা", "bho" to "टेराकोटा", "mai" to "टेराकोटा"
        ),
        "cat_jewellery" to mapOf(
            "en" to "Jewellery", "hi" to "आभूषण", "bn" to "গহনা", "ta" to "நகைகள்", "te" to "ఆభరణాలు",
            "mr" to "दागिने", "gu" to "ઘરેણાં", "kn" to "ಆಭರಣಗಳು", "ml" to "ആഭരണങ്ങൾ", "pa" to "ਗਹਿਣੇ",
            "or" to "ଅଳଙ୍କାର", "ur" to "زیورات", "as" to "অলংকাৰ", "bho" to "गहना", "mai" to "गहना"
        ),
        "cat_gi" to mapOf(
            "en" to "GI Certified", "hi" to "जीआई प्रमाणित", "bn" to "জিআই প্রত্যয়িত", "ta" to "ஜிஐ சான்றளிக்கப்பட்டது", "te" to "జిఐ ధృవీకరించబడింది",
            "mr" to "जीआय प्रमाणित", "gu" to "જીઆઈ પ્રમાણિત", "kn" to "ಜಿಐ ಪ್ರಮಾಣೀಕೃತ", "ml" to "ജിഐ സാക്ഷ്യപ്പെടുത്തിയത്", "pa" to "ਜੀਆਈ ਪ੍ਰਮਾਣਿਤ",
            "or" to "ଜିଆଇ ପ୍ରମାଣିତ", "ur" to "جی آئی سرٹیفائیڈ", "as" to "জিআই প্ৰমাণিত", "bho" to "जीआई प्रमाणित", "mai" to "जीआई प्रमाणित"
        ),

        // Product Details & Badges
        "in_stock_label" to mapOf(
            "en" to "In Stock", "hi" to "उपलब्ध", "bn" to "মজুদ আছে", "ta" to "கையிருப்பில்", "te" to "అందుబాటులో ఉంది",
            "mr" to "उपलब्ध", "gu" to "સ્ટોકમાં છે", "kn" to "ಲಭ್ಯವಿದೆ", "ml" to "ലഭ്യമാണ്", "pa" to "ਮੌਜੂਦ ਹੈ",
            "or" to "ମହଜୁଦ ଅଛି", "ur" to "دستیاب ہے", "as" to "মজুত আছে", "bho" to "उपलब्ध बा", "mai" to "उपलब्ध अछि"
        ),
        "category_label" to mapOf(
            "en" to "Category", "hi" to "श्रेणी", "bn" to "বিভাগ", "ta" to "பிரிவு", "te" to "వర్గం",
            "mr" to "वर्ग", "gu" to "શ્રેણી", "kn" to "ವರ್ಗ", "ml" to "വിഭാഗം", "pa" to "ਸ਼੍ਰੇਣੀ",
            "or" to "ବର୍ଗ", "ur" to "قسم", "as" to "শ্ৰেণী", "bho" to "श्रेणी", "mai" to "श्रेणी"
        ),
        "by_artisan" to mapOf(
            "en" to "By", "hi" to "द्वारा", "bn" to "দ্বারা", "ta" to "மூலம்", "te" to "ద్వారా",
            "mr" to "यांच्याकडून", "gu" to "દ્વારા", "kn" to "ಇವರಿಂದ", "ml" to "രചിച്ചത്", "pa" to "ਵੱਲੋਂ",
            "or" to "ଦ୍ୱାରା", "ur" to "از طرف", "as" to "দ্বাৰা", "bho" to "द्वारा", "mai" to "द्वारा"
        ),
        "add_to_bag" to mapOf(
            "en" to "Add to Bag", "hi" to "बैग में जोड़ें", "bn" to "ব্যাগে যোগ করুন", "ta" to "பையில் சேர்", "te" to "బ్యాగ్‌కు జోడించండి",
            "mr" to "पिशवीत टाका", "gu" to "બેગમાં ઉમેરો", "kn" to "ಬ್ಯಾಗ್‌ಗೆ ಸೇರಿಸಿ", "ml" to "ബാഗിലേക്ക് ചേർക്കുക", "pa" to "ਝੋਲੇ ਵਿੱਚ ਪਾਓ",
            "or" to "ବ୍ୟାଗ୍‌ରେ ଯୋଡ଼ନ୍ତୁ", "ur" to "بیگ میں شامل کریں", "as" to "বেগত ভৰাওক", "bho" to "झोला में जोड़ीं", "mai" to "झोला में राखू"
        ),
        "added_to_bag" to mapOf(
            "en" to "Added to Bag", "hi" to "बैग में जोड़ा गया", "bn" to "ব্যাগে যোগ করা হয়েছে", "ta" to "பையில் சேர்க்கப்பட்டது", "te" to "బ్యాగ్‌కు జోడించబడింది",
            "mr" to "पिशवीत जोडले", "gu" to "બેગમાં ઉમેરાયું", "kn" to "ಬ್ಯಾಗ್‌ಗೆ ಸೇರಿಸಲಾಗಿದೆ", "ml" to "ബാഗിലേക്ക് ചേർത്തു", "pa" to "ਝੋਲੇ ਵਿੱਚ ਜੋੜਿਆ",
            "or" to "ବ୍ୟାଗ୍‌ରେ ଯୋଡାଗଲା", "ur" to "بیگ میں شامل کیا گیا", "as" to "বেগত যোগ কৰা হ'ল", "bho" to "झोला में जोड़ल गइल", "mai" to "झोला में जोड़ल गेल"
        ),
        "view_bag" to mapOf(
            "en" to "View Bag", "hi" to "बैग देखें", "bn" to "ব্যাগ দেখুন", "ta" to "பையை பார்க்க", "te" to "బ్యాగ్ చూడండి",
            "mr" to "पिशवी पहा", "gu" to "બેગ જુઓ", "kn" to "ಬ್ಯಾಗ್ ನೋಡಿ", "ml" to "ബാഗ് കാണുക", "pa" to "ਝੋਲਾ ਵੇਖੋ",
            "or" to "ବ୍ୟାଗ୍ ଦେଖନ୍ତୁ", "ur" to "بیگ دیکھیں", "as" to "বেগ চাওক", "bho" to "झोला देखीं", "mai" to "झोला देखू"
        ),

        // Seller Listings & Dashboard
        "my_craft_listings" to mapOf(
            "en" to "My Craft Listings", "hi" to "मेरी शिल्प सूचियां", "bn" to "আমার শিল্প তালিকা", "ta" to "என் கைவினைப் பட்டியல்கள்", "te" to "నా కళాఖండాల జాబితా",
            "mr" to "माझ्या हस्तकला याद्या", "gu" to "મારી કારીગરી સૂચિ", "kn" to "ನನ್ನ ಕರಕುಶಲ ಪಟ್ಟಿ", "ml" to "എന്റെ ശില്പങ്ങൾ", "pa" to "ਮੇਰੀਆਂ ਕਲਾ ਸੂਚੀਆਂ",
            "or" to "ମୋର କଳା ତାଲିକା", "ur" to "میری مصنوعات کی فہرست", "as" to "মোৰ শিল্প তালিকা", "bho" to "हमार कलाकारी सूची", "mai" to "हमर शिल्प सूची"
        ),
        "products_in_store" to mapOf(
            "en" to "Products in Online Store", "hi" to "ऑनलाइन स्टोर में उपलब्ध उत्पाद", "bn" to "অনলাইন স্টোরে পণ্য", "ta" to "ஆன்லைன் கடையில் உள்ள பொருட்கள்", "te" to "ఆన్‌లైన్ స్టోర్‌లోని ఉత్పత్తులు",
            "mr" to "ऑनलाइन स्टोअरमधील उत्पादने", "gu" to "ઓનલાઇન સ્ટોરમાં ઉત્પાદનો", "kn" to "ಆನ್‌ಲೈನ್ ಅಂಗಡಿಯಲ್ಲಿರುವ ವಸ್ತುಗಳು", "ml" to "ഓൺലൈൻ സ്റ്റോറിലെ ഉൽപ്പന്നങ്ങൾ", "pa" to "ਆਨਲਾਈਨ ਸਟੋਰ ਵਿੱਚ ਉਤਪਾਦ",
            "or" to "ଅନଲାଇନ୍ ଷ୍ଟୋରରେ ଥିବା ସାମଗ୍ରୀ", "ur" to "آن لائن اسٹور میں مصنوعات", "as" to "অনলাইন দোকানত সামগ্ৰী", "bho" to "ऑनलाइन दुकान में समान", "mai" to "ऑनलाइन दुकान में समान"
        ),
        "list_new_craft" to mapOf(
            "en" to "List New Craft", "hi" to "नया शिल्प जोड़ें", "bn" to "নতুন শিল্প যোগ করুন", "ta" to "புதிய கைவினையைச் சேர்", "te" to "కొత్త కళాఖండాన్ని జోడించండి",
            "mr" to "नवीन हस्तकला जोडा", "gu" to "નવી કારીગરી ઉમેરો", "kn" to "ಹೊಸ ಕರಕುಶಲ ಸೇರಿಸಿ", "ml" to "പുതിയ ശില്പം ചേർക്കുക", "pa" to "ਨਵੀਂ ਕਲਾਕਾਰੀ ਜੋੜੋ",
            "or" to "ନୂତନ କଳା ଯୋଡନ୍ତୁ", "ur" to "نئی مصنوعات شامل کریں", "as" to "নতুন শিল্প যোগ কৰক", "bho" to "नया शिल्प जोड़ीं", "mai" to "नब शिल्प जोड़ू"
        ),
        "artisan_dashboard" to mapOf(
            "en" to "Artisan Dashboard", "hi" to "कारीगर डैशबोर्ड", "bn" to "কারিগর ড্যাশবোর্ড", "ta" to "கைவினைஞர் டாஷ்போர்டு", "te" to "కళాకారుడి డాష్‌బోర్డ్",
            "mr" to "कारागीर डॅशबोर्ड", "gu" to "કારીગર ડેશબોર્ડ", "kn" to "ಕುಶಲಕರ್ಮಿ ಡ್ಯಾಶ್‌ಬೋರ್ಡ್", "ml" to "ശില്പി ഡാഷ്‌ബോർഡ്", "pa" to "ਕਾਰੀਗਰ ਡੈਸ਼ਬੋਰਡ",
            "or" to "ଶିଳ୍ପୀ ଡ୍ୟାସବୋର୍ଡ", "ur" to "دستکار ڈیش بورڈ", "as" to "শিল্পী ডেচবৰ্ড", "bho" to "कारीगर डैशबोर्ड", "mai" to "कारीगर डैशबोर्ड"
        ),
        "live_revenue_tracking" to mapOf(
            "en" to "Live Revenue & Order Tracking", "hi" to "लाइव आय एवं ऑर्डर ट्रैकिंग", "bn" to "লাইভ আয় ও অর্ডার ট্র্যাকিং",
            "ta" to "நேரலை வருவாய் மற்றும் ஆர்டர் கண்காணிப்பு", "te" to "ప్రత్యక్ష ఆదాయం & ఆర్డర్ ట్రాకింగ్", "mr" to "थेट उत्पन्न आणि ऑर्डर ट्रॅकिंग",
            "gu" to "લાઇવ આવક અને ઓર્ડર ટ્રેકિંગ", "kn" to "ಲೈವ್ ಆದಾಯ ಮತ್ತು ಆರ್ಡರ್ ಟ್ರ್ಯಾಕಿಂಗ್", "ml" to "തത്സമയ വരുമാനവും ഓർഡർ വിവരങ്ങളും",
            "pa" to "ਲਾਈਵ ਕਮਾਈ ਅਤੇ ਆਰਡਰ ਟ੍ਰੈਕਿੰਗ", "or" to "ଲାଇଭ୍ ଆୟ ଏବଂ ଅର୍ଡର ଟ୍ରାକିଂ", "ur" to "لائیو آمدنی اور آرڈر ٹریکنگ",
            "as" to "লাইভ উপাৰ্জন আৰু অৰ্ডাৰ ট্ৰেকিং", "bho" to "लाइव कमाई आ ऑर्डर ट्रैकिंग", "mai" to "लाइव कमाई आ ऑर्डर ट्रैकिंग"
        ),
        "this_month_payout" to mapOf(
            "en" to "This Month Payout", "hi" to "इस महीने का भुगतान", "bn" to "এই মাসের পেমেন্ট", "ta" to "இந்த மாத கொடுப்பனவு", "te" to "ఈ నెల చెల్లింపు",
            "mr" to "या महिन्याचे उत्पन्न", "gu" to "આ મહિનાની ચૂકવણી", "kn" to "ಈ ತಿಂಗಳ ಪಾವತಿ", "ml" to "ഈ മാസത്തെ പേയ്‌മെന്റ്", "pa" to "ਇਸ ਮਹੀਨੇ ਦੀ ਅਦਾਇਗੀ",
            "or" to "ଏହି ମାସର ପାଉଣା", "ur" to "اس ماہ کی ادائیگی", "as" to "এই মাহৰ পাৰিতোষিক", "bho" to "एही महीना के भुगतान", "mai" to "एहि मासक भुगतान"
        ),
        "active_orders_label" to mapOf(
            "en" to "Active Orders", "hi" to "सक्रिय ऑर्डर्स", "bn" to "সক্রিয় অর্ডার", "ta" to "செயலில் உள்ள ஆர்டர்கள்", "te" to "యాక్టివ్ ఆర్డర్లు",
            "mr" to "सक्रिय ऑर्डर्स", "gu" to "સક્રિય ઓર્ડર", "kn" to "ಸಕ್ರಿಯ ಆರ್ಡರ್‌ಗಳು", "ml" to "സജീവ ഓർഡറുകൾ", "pa" to "ਐਕਟਿਵ ਆਰਡਰ",
            "or" to "ସକ୍ରିୟ ଅର୍ଡର", "ur" to "فعال آرڈرز", "as" to "সক্ৰিয় অৰ্ডাৰ", "bho" to "चालू ऑर्डर", "mai" to "सक्रिय ऑर्डर"
        ),
        "ready_to_ship_label" to mapOf(
            "en" to "ready to ship", "hi" to "भेजने के लिए तैयार", "bn" to "পাঠানোর জন্য প্রস্তুত", "ta" to "அனுப்ப தயார்", "te" to "రవాణాకు సిద్ధం",
            "mr" to "पाठवण्यासाठी सज्ज", "gu" to "મોકલવા માટે તૈયાર", "kn" to "ರವಾನೆಗೆ ಸಿದ್ಧ", "ml" to "അയക്കാൻ തയ്യാറാണ്", "pa" to "ਭੇਜਣ ਲਈ ਤਿਆਰ",
            "or" to "ପଠାଇବାକୁ ପ୍ରସ୍ତୁତ", "ur" to "بھیجنے کے لیے تیار", "as" to "পঠিয়াবলৈ সাজু", "bho" to "भेजे खातिर तैयार", "mai" to "पठेबाक लेल तैयार"
        ),
        "live_customer_orders" to mapOf(
            "en" to "Live Customer Orders", "hi" to "ग्राहकों के लाइव ऑर्डर्स", "bn" to "গ্রাহকদের লাইভ অর্ডার", "ta" to "வாடிக்கையாளர் ஆர்டர்கள்", "te" to "కస్టమర్ లైవ్ ఆర్డర్లు",
            "mr" to "ग्राहकांच्या थेट ऑर्डर्स", "gu" to "ગ્રાહકોના લાઈવ ઓર્ડર", "kn" to "ಗ್ರಾಹಕರ ಲೈವ್ ಆರ್ಡರ್‌ಗಳು", "ml" to "ഉപഭോക്തൃ ഓർഡറുകൾ", "pa" to "ਗਾਹਕਾਂ ਦੇ ਆਰਡਰ",
            "or" to "ଗ୍ରାହକଙ୍କ ଲାଇଭ୍ ଅର୍ଡର", "ur" to "صارفین کے لائیو آرڈرز", "as" to "গ্ৰাহকৰ লাইভ অৰ্ডাৰ", "bho" to "ग्राहक लोगन के ऑर्डर", "mai" to "ग्राहकक लाइव ऑर्डर"
        ),

        // Cart & Checkout
        "your_bag_empty" to mapOf(
            "en" to "Your bag is empty", "hi" to "आपका बैग खाली है", "bn" to "আপনার ব্যাগ খালি", "ta" to "உங்கள் பை காலியாக உள்ளது", "te" to "మీ బ్యాగ్ ఖాళీగా ఉంది",
            "mr" to "तुमची पिशवी रिकामी आहे", "gu" to "તમારી બેગ ખાલી છે", "kn" to "ನಿಮ್ಮ ಬ್ಯಾಗ್ ಖಾಲಿಯಾಗಿದೆ", "ml" to "നിങ്ങളുടെ ബാഗ് ശൂന്യമാണ്", "pa" to "ਤੁਹਾਡਾ ਝੋਲਾ ਖਾਲੀ ਹੈ",
            "or" to "ଆପଣଙ୍କ ବ୍ୟାଗ୍ ଖାଲି ଅଛି", "ur" to "آپ کا بیگ خالی ہے", "as" to "আপোনাৰ বেগ খালী", "bho" to "रउआ के झोला खाली बा", "mai" to "अहाँक झोला खाली अछि"
        ),
        "explore_crafts_btn" to mapOf(
            "en" to "Explore Crafts", "hi" to "शिल्प देखें", "bn" to "শিল্প দেখুন", "ta" to "கைவினைகளை ஆராயுங்கள்", "te" to "కళలను అన్వేషించండి",
            "mr" to "हस्तकला एक्सप्लोर करा", "gu" to "કારીગરી જુઓ", "kn" to "ಕರಕುಶಲ ಅನ್ವೇಷಿಸಿ", "ml" to "ശില്പങ്ങൾ കാണുക", "pa" to "ਕਲਾਕਾਰੀ ਵੇਖੋ",
            "or" to "ହସ୍ତତନ୍ତ ଦେଖନ୍ତୁ", "ur" to "دستکاری دریافت کریں", "as" to "শিল্প চাওক", "bho" to "शिल्प देखीं", "mai" to "शिल्प देखू"
        ),
        "order_summary" to mapOf(
            "en" to "Order Summary", "hi" to "ऑर्डर सारांश", "bn" to "অর্ডারের সারাংশ", "ta" to "ஆர்டர் சுருக்கம்", "te" to "ఆర్డర్ సారాంశం",
            "mr" to "ऑर्डर सारांश", "gu" to "ઓર્ડર સારાંશ", "kn" to "ಆರ್ಡರ್ ಸಾರಾಂಶ", "ml" to "ഓർഡർ സംഗ്രഹം", "pa" to "ਆਰਡਰ ਸੰਖੇਪ",
            "or" to "ଅର୍ଡର ସାରାଂଶ", "ur" to "آرڈر کا خلاصہ", "as" to "অৰ্ডাৰ সাৰাংশ", "bho" to "ऑर्डर सारांश", "mai" to "ऑर्डर सारांश"
        ),
        "subtotal" to mapOf(
            "en" to "Subtotal", "hi" to "उप-योग", "bn" to "মোট মূল্য", "ta" to "கூட்டுத்தொகை", "te" to "ఉపమొత్తం",
            "mr" to "एकूण रक्कम", "gu" to "કુલ રકમ", "kn" to "ಉಪಮೊತ್ತ", "ml" to "ആകെ തുക", "pa" to "ਕੁੱਲ ਰਕਮ",
            "or" to "ମୋଟ", "ur" to "ذیلی کل", "as" to "মুঠ", "bho" to "उप-योग", "mai" to "उप-योग"
        ),
        "delivery" to mapOf(
            "en" to "Delivery", "hi" to "वितरण शुल्क", "bn" to "ডেলিভারি", "ta" to "டெலிவரி", "te" to "డెలివరీ",
            "mr" to "डिलिव्हरी", "gu" to "ડિલિવરી", "kn" to "ವಿತರಣೆ", "ml" to "ഡെലിവറി", "pa" to "ਡਿਲੀਵਰੀ",
            "or" to "ଡେଲିଭରି", "ur" to "ڈیلیوری", "as" to "ডেলিভাৰী", "bho" to "पहुंचावे के खर्चा", "mai" to "पहुंचाबय के खर्चा"
        ),
        "free" to mapOf(
            "en" to "Free", "hi" to "निःशुल्क", "bn" to "বিনামূল্যে", "ta" to "இலவசம்", "te" to "ఉచితం",
            "mr" to "मोफत", "gu" to "મફત", "kn" to "ಉಚಿತ", "ml" to "സൗജന്യം", "pa" to "ਮੁਫ਼ਤ",
            "or" to "ମାଗଣା", "ur" to "مفت", "as" to "বিনামূলীয়া", "bho" to "मुफ्त", "mai" to "मुफ्त"
        ),
        "total_amount" to mapOf(
            "en" to "Total Amount", "hi" to "कुल राशि", "bn" to "মোট প্রদেয়", "ta" to "மொத்த தொகை", "te" to "మొత్తం మొత్తం",
            "mr" to "एकूण देय", "gu" to "કુલ રકમ", "kn" to "ಒಟ್ಟು ಮೊತ್ತ", "ml" to "ആകെ നൽകേണ്ടത്", "pa" to "ਕੁੱਲ ਭੁਗਤਾਨ",
            "or" to "ମୋଟ ଦେୟ", "ur" to "کل رقم", "as" to "মুঠ পৰিশোধ", "bho" to "कुल पईसा", "mai" to "कुल राशि"
        ),
        "proceed_checkout" to mapOf(
            "en" to "Proceed to Checkout (Demo Order)", "hi" to "चेकआउट करें (डेमो ऑर्डर)", "bn" to "চেকআউট করুন (ডেমো অর্ডার)",
            "ta" to "பணம் செலுத்த தொடரவும்", "te" to "చెక్‌అవుట్‌కు వెళ్లండి", "mr" to "पुढील प्रक्रिया करा (डेमो)",
            "gu" to "ચેકઆઉટ કરો (ડેમો ઓર્ડર)", "kn" to "ಖರೀದಿಸಿ (ಡೆಮೊ ಆರ್ಡರ್)", "ml" to "വാങ്ങുക (ഡെമോ ഓർഡർ)",
            "pa" to "ਚੈੱਕਆਉਟ ਕਰੋ (ਡੈਮੋ ਆਰਡਰ)", "or" to "ଚେକଆଉଟ୍ କରନ୍ତୁ", "ur" to "آرڈر مکمل کریں (ڈیمو)",
            "as" to "অৰ্ডাৰ সম্পূৰ্ণ কৰক", "bho" to "ऑर्डर करीं (डेमो)", "mai" to "ऑर्डर करू (डेमो)"
        ),

        // Profile Options
        "saved_addresses" to mapOf(
            "en" to "Saved Addresses", "hi" to "सहेजे गए पते", "bn" to "সংরক্ষিত ঠিকানা", "ta" to "சேமிக்கப்பட்ட முகவரிகள்", "te" to "సేవ్ చేసిన చిరునామాలు",
            "mr" to "जतन केलेले पत्ते", "gu" to "સાચવેલા સરનામાં", "kn" to "ಉಳಿಸಿದ ವಿಳಾಸಗಳು", "ml" to "സൂക്ഷിച്ച വിലാസങ്ങൾ", "pa" to "ਸੁਰੱਖਿਅਤ ਪਤੇ",
            "or" to "ସଂରକ୍ଷିତ ଠିକଣା", "ur" to "محفوظ شدہ پتے", "as" to "সংৰক্ষিত ঠিকনা", "bho" to "सहेजल पता", "mai" to "सहेजल पता"
        ),
        "bank_upi" to mapOf(
            "en" to "Bank & UPI Payouts", "hi" to "बैंक एवं यूपीआई भुगतान", "bn" to "ব্যাঙ্ক এবং ইউপিআই পেমেন্ট", "ta" to "வங்கி மற்றும் யுபிஐ", "te" to "బ్యాంక్ మరియు యుపిఐ చెల్లింపులు",
            "mr" to "बँक आणि यूपीआय", "gu" to "બેંક અને યુપીઆઈ", "kn" to "ಬ್ಯಾಂಕ್ ಮತ್ತು ಯುಪಿಐ", "ml" to "ബാങ്കും യുപിഐയും", "pa" to "ਬੈਂਕ ਅਤੇ ਯੂਪੀਆਈ",
            "or" to "ବ୍ୟାଙ୍କ ଏବଂ ୟୁପିଆଇ", "ur" to "بینک اور یو پی آئی", "as" to "বেংক আৰু ইউপিআই", "bho" to "बैंक आ यूपीआई", "mai" to "बैंक आ यूपीआई"
        ),
        "artisan_helpline" to mapOf(
            "en" to "Artisan Guild Helpline", "hi" to "कारीगर गिल्ड हेल्पलाइन", "bn" to "কারিগর হেল্পলাইন", "ta" to "கைவினைஞர் உதவி மையம்", "te" to "కళాకారుల హెల్ప్‌లైన్",
            "mr" to "कारागीर मदत केंद्र", "gu" to "કારીગર હેલ્પલાઇન", "kn" to "ಕುಶಲಕರ್ಮಿ ಸಹಾಯವಾಣಿ", "ml" to "ശില്പി ഹെൽപ്പ്‌ലൈൻ", "pa" to "ਕਾਰੀਗਰ ਹੈਲਪਲਾਈਨ",
            "or" to "ଶିଳ୍ପୀ ହେଲ୍ପଲାଇନ", "ur" to "دستکار ہیلپ لائن", "as" to "শিল্পী হেল্পলাইন", "bho" to "कारीगर हेल्पलाइन", "mai" to "कारीगर हेल्पलाइन"
        ),
        "gi_guarantee" to mapOf(
            "en" to "GI & Fair Trade Guarantee", "hi" to "जीआई एवं निष्पक्ष व्यापार गारंटी", "bn" to "জিআই ও ন্যায্য বাণিজ্য গ্যারান্টি",
            "ta" to "ஜிஐ மற்றும் நியாயமான வர்த்தக உத்தரவாதம்", "te" to "జిఐ & ఫెయిర్ ట్రేడ్ గ్యారెంటీ", "mr" to "जीआय आणि फेअर ट्रेड हमी",
            "gu" to "જીઆઈ અને ફેર ટ્રેડ ગેરંટી", "kn" to "ಜಿಐ ಮತ್ತು ನ್ಯಾಯಯುತ ವ್ಯಾಪಾರ ಖಾತರಿ", "ml" to "ന്യായമായ വ്യാപാര ഉറപ്പ്",
            "pa" to "ਜੀਆਈ ਅਤੇ ਨਿਰਪੱਖ ਵਪਾਰ ਗਾਰੰਟੀ", "or" to "ଜିଆଇ ଏବଂ ଉଚିତ ବ୍ୟବସାୟ ଗ୍ୟାରେଣ୍ଟି", "ur" to "جی آئی اور منصفانہ تجارت کی ضمانت",
            "as" to "জিআই আৰু উচিত বাণিজ্য গেৰাণ্টী", "bho" to "जीआई आ निष्पक्ष व्यापार गारंटी", "mai" to "जीआई आ निष्पक्ष व्यापार गारंटी"
        ),

        // Auth
        "artisan_test_creds" to mapOf(
            "en" to "Artisan Test Credentials", "hi" to "कारीगर टेस्ट क्रेडेंशियल्स", "bn" to "কারিগর টেস্ট লগইন", "ta" to "கைவினைஞர் சோதனை உள்நுழைவு", "te" to "కళాకారుడి లాగిన్ ఆధారాలు",
            "mr" to "कारागीर चाचणी क्रेडेंशियल्स", "gu" to "કારીગર ટેસ્ટ લૉગિન", "kn" to "ಕುಶಲಕರ್ಮಿ ಪರೀಕ್ಷಾ ಲಾಗಿನ್", "ml" to "ശില്പി ലോഗിൻ വിവരങ്ങൾ", "pa" to "ਕਾਰੀਗਰ ਟੈਸਟ ਲੌਗਇਨ",
            "or" to "ଶିଳ୍ପୀ ଟେଷ୍ଟ କ୍ରେଡେନ୍ସିଆଲ୍", "ur" to "دستکار ٹیسٹ لاگ ان", "as" to "শিল্পী টেষ্ট লগইন", "bho" to "कारीगर टेस्ट लॉगिन", "mai" to "कारीगर टेस्ट लॉगिन"
        ),
        "buyer_test_creds" to mapOf(
            "en" to "Buyer Test Credentials", "hi" to "खरीदार टेस्ट क्रेडेंशियल्स", "bn" to "ক্রেতা টেস্ট লগইন", "ta" to "வாங்குபவர் சோதனை உள்நுழைவு", "te" to "కొనుగోలుదారు లాగిన్ ఆధారాలు",
            "mr" to "ग्राहक चाचणी क्रेडेंशियल्स", "gu" to "ગ્રાહક ટેસ્ટ લૉગિન", "kn" to "ಖರೀದಿದಾರ ಪರೀಕ್ಷಾ ಲಾಗಿನ್", "ml" to "ഉപഭോക്തൃ ലോഗിൻ വിവരങ്ങൾ", "pa" to "ਗਾਹਕ ਟੈਸਟ ਲੌਗਇਨ",
            "or" to "ଗ୍ରାହକ ଟେଷ୍ଟ କ୍ରେଡେନ୍ସିଆଲ୍", "ur" to "خریدار ٹیسٹ لاگ ان", "as" to "গ্ৰাহক টেষ্ট লগইন", "bho" to "खरीदार टेस्ट लॉगिन", "mai" to "खरीदार टेस्ट लॉगिन"
        ),
        "sms_testing_note" to mapOf(
            "en" to "SMS verification is restricted during testing. Please use the demo credentials below.",
            "hi" to "परीक्षण चरण के दौरान एसएमएस सत्यापन प्रतिबंधित है। कृपया नीचे दिए गए डेमो क्रेडेंशियल्स का उपयोग करें।",
            "bn" to "পরীক্ষার সময় এসএমএস যাচাইকরণ সীমাবদ্ধ। দয়া করে নীচের ডেমো লগইন ব্যবহার করুন।",
            "ta" to "சோதனையின் போது எஸ்எம்எஸ் கட்டுப்படுத்தப்பட்டுள்ளது. கீழே உள்ள டெமோவைப் பயன்படுத்தவும்.",
            "te" to "టెస్టింగ్ సమయంలో ఎస్ఎంఎస్ పరిమితం చేయబడింది. దయచేసి డెమో లాగిన్ ఉపయోగించండి.",
            "mr" to "चाचणी दरम्यान एसएमएस पडताळणी मर्यादित आहे. कृपया खालील डेमो तपशील वापरा.",
            "gu" to "પરીક્ષણ દરમિયાન એસએમએસ મર્યાદિત છે. કૃપા કરીને નીચે આપેલા ડેમો વિગતોનો ઉપયોગ કરો.",
            "kn" to "ಪರೀಕ್ಷೆಯ ಸಮಯದಲ್ಲಿ SMS ನಿರ್ಬಂಧಿಸಲಾಗಿದೆ. ದಯವಿಟ್ಟು ಕೆಳಗಿನ ಡೆಮೊ ಬಳಸಿ.",
            "ml" to "ടെസ്റ്റിംഗ് സമയത്ത് SMS പരിമിതമാണ്. താഴെയുള്ള ഡെമോ ഉപയോഗിക്കുക.",
            "pa" to "ਟੈਸਟਿੰਗ ਦੌਰਾਨ SMS ਸੀਮਤ ਹੈ। ਕਿਰਪਾ ਕਰਕੇ ਹੇਠਾਂ ਦਿੱਤੇ ਡੈਮੋ ਦੀ ਵਰਤੋਂ ਕਰੋ।",
            "or" to "ପରୀକ୍ଷଣ ସମୟରେ SMS ସୀମିତ ଅଛି | ଦୟାକରି ଡେମୋ ବ୍ୟବହାର କରନ୍ତୁ |",
            "ur" to "ٹیسٹنگ کے دوران ایس ایم ایس محدود ہے۔ برائے مہربانی نیچے دی گئی تفصیلات استعمال کریں۔",
            "as" to "পৰীক্ষাৰ সময়ত SMS সীমিত। অনুগ্ৰহ কৰি তলৰ ডেমো ব্যৱহাৰ কৰক।",
            "bho" to "टेस्टिंग में एसएमएस बंद बा। कृपया नीचे दिहल टेस्ट कोड इस्तेमाल करीं।",
            "mai" to "टेस्टिंग में एसएमएस बंद अछि। कृपया नीचा देल कोड इस्तेमाल करू।"
        ),
        "continue_btn" to mapOf(
            "en" to "Continue", "hi" to "आगे बढ़ें", "bn" to "এগিয়ে যান", "ta" to "தொடரவும்", "te" to "కొనసాగించండి",
            "mr" to "पुढे चला", "gu" to "આગળ વધો", "kn" to "ಮುಂದುವರಿಯಿರಿ", "ml" to "തുടരുക", "pa" to "ਅੱਗੇ ਵਧੋ",
            "or" to "ଆଗକୁ ବଢ଼ନ୍ତୁ", "ur" to "آگے بڑھیں", "as" to "আগবাঢ়ক", "bho" to "आगे बढ़ीं", "mai" to "आगे बढ़ू"
        ),
        "banner1_title" to mapOf("en" to "Artisan Handloom Week", "hi" to "कारीगर हथकरघा सप्ताह", "bn" to "তাঁত শিল্প সপ্তাহ"),
        "banner1_sub" to mapOf("en" to "Preserving Centuries of Heritage Crafts", "hi" to "सदियों पुरानी पारंपरिक शिल्प कला का संरक्षण"),
        "banner1_badge" to mapOf("en" to "HANDLOOM SPECIAL", "hi" to "हथकरघा विशेष", "bn" to "তাঁত বিশেষ"),
        "banner2_title" to mapOf("en" to "Authentic Madhubani Art", "hi" to "प्रामाणिक मधुबनी कला", "bn" to "প্রামাণিক মধুবনী শিল্প"),
        "banner2_sub" to mapOf("en" to "Handcrafted by Master Folk Artists", "hi" to "सिद्धहस्त लोक कलाकारों द्वारा हस्तनिर्मित"),
        "banner2_badge" to mapOf("en" to "GI CERTIFIED CRAFTS", "hi" to "जीआई प्रमाणित शिल्प"),
        "banner3_title" to mapOf("en" to "Terracotta & Metalwork", "hi" to "टेराकोटा एवं धातु शिल्प"),
        "banner3_sub" to mapOf("en" to "Direct Workshop Pricing from Rural Artisans", "hi" to "ग्रामीण कारीगरों से सीधे कार्यशाला मूल्य पर"),
        "banner3_badge" to mapOf("en" to "DIRECT ARTISAN SALE", "hi" to "कारीगरों द्वारा सीधी बिक्री"),
        "the_story" to mapOf("en" to "THE STORY (KAHAANI)", "hi" to "शिल्प की कहानी (कथा)", "bn" to "শিল্পের ইতিহাস ও গল্প", "ta" to "கைவினை கதை"),
        "craft_specs" to mapOf("en" to "CRAFT SPECIFICATIONS", "hi" to "शिल्प की विशेषताएं", "bn" to "পণ্যের বিবরণ", "ta" to "கைவினை விவரங்கள்"),
        "buy_craft" to mapOf("en" to "Buy Craft", "hi" to "शिल्प खरीदें", "bn" to "কিনুন", "ta" to "வாங்கவும்", "te" to "కొనండి", "mr" to "खरेदी करा", "gu" to "ખરીદો"),
        "who_are_you" to mapOf("en" to "Who are you?", "hi" to "आप कौन हैं?", "bn" to "আপনি কে?", "ta" to "நீங்கள் யார்?"),
        "im_artisan" to mapOf("en" to "I'm an Artisan", "hi" to "मैं कारीगर हूँ", "bn" to "আমি একজন কারিগর", "ta" to "நான் ஒரு கைவினைஞர்"),
        "im_artisan_sub" to mapOf("en" to "I make & sell crafts", "hi" to "मैं शिल्प बनाता और बेचता हूँ", "bn" to "আমি হস্তশিল্প তৈরি করি"),
        "im_buyer" to mapOf("en" to "I'm a Buyer", "hi" to "मैं खरीदार हूँ", "bn" to "আমি একজন ক্রেতা", "ta" to "நான் வாங்குபவர்"),
        "im_buyer_sub" to mapOf("en" to "I buy handmade crafts", "hi" to "मैं हस्तशिल्प खरीदता हूँ", "bn" to "আমি হস্তশিল্প কিনি"),
        "enter_otp" to mapOf("en" to "Enter OTP Code", "hi" to "ओटीपी कोड दर्ज करें", "bn" to "ওটিপি লিখুন", "ta" to "OTP குறியீட்டை உள்ளிடவும்"),
        "per_piece" to mapOf("en" to "per piece", "hi" to "प्रति नग", "bn" to "প্রতি পিস", "ta" to "ஒரு துண்டுக்கு"),
        "reviews_suffix" to mapOf("en" to "reviews", "hi" to "समीक्षाएं", "bn" to "পর্যালোচনা"),
        "handmade" to mapOf("en" to "Handmade", "hi" to "हस्तनिर्मित", "bn" to "হাতে তৈরি", "ta" to "கைவினை"),
        "publish_craft" to mapOf("en" to "Publish to Dhaaga + ONDC", "hi" to "शिल्प प्रकाशित करें (Dhaaga + ONDC)", "bn" to "শিল্প প্রকাশ করুন", "ta" to "வெளியிடவும்")
    )

    fun translate(key: String, langCode: String, fallback: String = key): String {
        if (langCode == "en") return fallback.ifBlank { key }
        val dict = translations[key]
        if (dict != null) {
            val regional = dict[langCode]
            if (!regional.isNullOrBlank()) return regional
            val hindiFallback = dict["hi"]
            if (!hindiFallback.isNullOrBlank()) return hindiFallback
        }
        return fallback.ifBlank { key }
    }

    fun getLanguageName(code: String): String {
        val lang = SUPPORTED_LANGUAGES.find { it.code == code }
        return if (lang != null) "${lang.nativeName} (${lang.englishName})" else "English"
    }
}
