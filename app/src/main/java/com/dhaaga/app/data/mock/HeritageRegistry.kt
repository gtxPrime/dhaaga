package com.dhaaga.app.data.mock

import com.dhaaga.app.data.model.CraftHeritageModel
import com.dhaaga.app.data.model.CraftMaterial
import com.dhaaga.app.data.model.CraftStep
import com.dhaaga.app.data.model.CraftSymbol

/**
 * Curated registry of India's living cultural traditions and heritage craft records.
 * Directly supports SIH Problem Statement 26197 (Heritage & Culture).
 */
object HeritageRegistry {

    val livingTraditions: List<CraftHeritageModel> = listOf(
        // ── 1. Warli Folk Art (Maharashtra) ─────────────────────────────────
        CraftHeritageModel(
            craftId = "heritage_warli_01",
            craftNameEn = "Warli Tribal Folk Painting",
            craftNameHi = "वारली जनजातीय लोक चित्रकला",
            category = "Folk Painting",
            state = "Maharashtra",
            district = "Palghar",
            region = "Western Ghats Tribal Belt",
            community = "Warli & Malhar Koli Tribes",
            traditionAgeYears = 2500,
            summaryEn = "2,500-year-old sacred geometric tribal art portraying cosmic harmony, harvest celebrations, and everyday village life using natural rice paste.",
            summaryHi = "ढाई हज़ार वर्ष पुरानी पवित्र ज्यामितीय जनजातीय कला, जो चावल के लेप से प्रकृति, फसल उत्सव और ग्राम जीवन का चित्रण करती है।",
            storyKahaaniEn = "Warli painting is not merely decorative; it is an oral narrative painted on mud walls. Passed down matrilineally across generations, women known as 'Suhasinis' created these paintings during weddings to invite blessings from Palaghat, the goddess of fertility. Warli art avoids mythological deities, celebrating instead the eternal cycle of mother nature, forests, wildlife, and community harmony.",
            storyKahaaniHi = "वारली चित्रकला केवल सजावट नहीं, बल्कि मिट्टी की दीवारों पर उकेरी गई मौखिक परम्परा है। पीढ़ियों से माताओं द्वारा बेटियों को सिखाई गई इस विधा में 'सुहासिनी' महिलाएं विवाह और उत्सवों पर प्रकृति और कुलदेवी पालाघाट के आशीर्वाद हेतु चित्र बनाती हैं। इसमें पौराणिक कथाओं के स्थान पर जल, जंगल, जमीन और सामूहिक जीवन की महिमा का गान होता है।",
            culturalSignificance = "Sacred ritual folk art celebrating human communion with nature. Symbolizes ecological sustainability and community solidarity.",
            rawMaterials = listOf(
                CraftMaterial(
                    nameEn = "Fermented Rice Paste",
                    nameHi = "चावल का लेप (पिठी)",
                    source = "Pounded indigenous rice mixed with water and natural acacia gum binder",
                    isEcoFriendly = true,
                    preparationNote = "Ground fine on stone mortar; fermented overnight for luminous white opacity."
                ),
                CraftMaterial(
                    nameEn = "Red Ochre (Geru) Mud",
                    nameHi = "गेरू मिट्टी व गोबर का लेप",
                    source = "Locally mined terracotta earth mixed with water and dry cow dung wash",
                    isEcoFriendly = true,
                    preparationNote = "Applied as the foundational canvas wash on bamboo or handmade paper."
                ),
                CraftMaterial(
                    nameEn = "Chewed Bamboo Twig (Baharu)",
                    nameHi = "बांस की कूंची (बहरू)",
                    source = "Tender bamboo twig softened at the tip by chewing",
                    isEcoFriendly = true,
                    preparationNote = "Acts as a capillary stylus for microscopic line work."
                )
            ),
            toolsUsed = listOf("Bamboo Stylus (Baharu)", "Stone Pestle", "Earthen Mixing Bowls"),
            processSteps = listOf(
                CraftStep(
                    stepNumber = 1,
                    titleEn = "Base Preparation (Lepan)",
                    titleHi = "धरातल लेपन",
                    descriptionEn = "Coating the handmade paper or wall surface with natural Geru and earth binder to achieve a warm rust-brown tone.",
                    descriptionHi = "हाथ से बने कागज या दीवार पर गेरू और प्राकृतिक घोल का लेप लगाना ताकि एक सौम्य मिट्टी का रंग मिल सके।",
                    durationMin = 20
                ),
                CraftStep(
                    stepNumber = 2,
                    titleEn = "Mixing Rice Pigment",
                    titleHi = "चावल के वर्णक का निर्माण",
                    descriptionEn = "Pounding rice flour with gum acacia and pure water to create non-cracking white pigment.",
                    descriptionHi = "बारीक पिसे चावल के आटे में बबूल का गोंद मिलाकर शुद्ध सफेद रंग तैयार करना।",
                    durationMin = 15
                ),
                CraftStep(
                    stepNumber = 3,
                    titleEn = "Constructing Geometric Anatomy",
                    titleHi = "ज्यामितीय संरचना",
                    descriptionEn = "Drafting human and animal figures using two inverted triangles joined at the tip, signifying cosmic balance.",
                    descriptionHi = "दो विपरीत त्रिभुजों को जोड़कर मानव व पशु आकृतियां बनाना, जो प्रकृति के संतुलन का प्रतीक हैं।",
                    durationMin = 45
                ),
                CraftStep(
                    stepNumber = 4,
                    titleEn = "Drawing the Spiral Tarpa Dance",
                    titleHi = "तारपा नृत्य का सर्पिल चक्र",
                    descriptionEn = "Painting continuous concentric circles of dancers moving rhythmically around the Tarpa player without beginning or end.",
                    descriptionHi = "तारपा वादक के चारों ओर अनवरत घूमते नर्तकों का चक्र बनाना, जो जीवन की निरंतरता को दर्शाता है।",
                    durationMin = 60
                )
            ),
            motifsAndSymbols = listOf(
                CraftSymbol(
                    symbolNameEn = "The Circle (Vritta)",
                    symbolNameHi = "चक्र (सूर्य व चंद्र)",
                    meaningEn = "Represents the Sun and Moon, divine light, and the eternal cycle of seasons.",
                    meaningHi = "सूर्य और चंद्रमा का प्रतीक, जो दिन-रात और ऋतु-परिवर्तन का शाश्वत चक्र दर्शाते हैं।"
                ),
                CraftSymbol(
                    symbolNameEn = "The Triangle (Trikon)",
                    symbolNameHi = "त्रिभुज (पर्वत व वृक्ष)",
                    meaningEn = "Derived from sacred Sahyadri hills and conical tree canopies. Two joined triangles form the human torso.",
                    meaningHi = "सह्याद्रि पर्वतमाला और नुकीले वृक्षों से प्रेरित। दो त्रिभुज मिलकर मानव देह का निर्माण करते हैं।"
                ),
                CraftSymbol(
                    symbolNameEn = "The Square (Chauk)",
                    symbolNameHi = "चौक (देवी पालाघाट का गृह)",
                    meaningEn = "The sacred central enclosure housing the corn mother / fertility deity Palaghat.",
                    meaningHi = "पवित्र वर्गाकार कोष्ठक, जिसमें अन्न और उर्वरता की देवी पालाघाट विराजती हैं।"
                ),
                CraftSymbol(
                    symbolNameEn = "Tarpa Spiral Dance",
                    symbolNameHi = "तारपा नृत्य मंडली",
                    meaningEn = "Community dancers holding hands in a spiral, symbolizing life's unbroken chain with no one above another.",
                    meaningHi = "एक दूसरे का हाथ थामे नर्तकों का घेरा, जो बिना किसी भेदभाव के एकता और भाईचारे का संदेश देता है।"
                )
            ),
            oralHistoryAudioUrl = "https://actions.google.com/sounds/v1/ambiences/outdoor_market.ogg",
            oralHistoryTranscriptHi = "हमारी दादी कहती थीं कि वारली केवल चित्र नहीं, प्रकृति और पुरखों से संवाद की हमारी भाषा है। जब हम बांस की सींक से चावल का लेप लगाते हैं, तो हम केवल रंग नहीं भरते, बल्कि धरती के प्रति कृतज्ञता व्यक्त करते हैं। यह कला हमें सिखाती है कि जीवन एक वृत्त है — कोई छोटा नहीं, कोई बड़ा नहीं।",
            oralHistoryTranscriptEn = "My grandmother taught me that Warli is not just a painting, it is our language of communion with nature and ancestors. When we stroke the bamboo stylus with rice paste, we are expressing gratitude to Mother Earth. It teaches us that life is a circle — nobody is higher, nobody is lower.",
            oralHistoryDurationSec = 195,
            oralNarratorName = "Savita Dhodi (3rd Gen Warli Master)",
            processVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
            bannerImageUrl = "https://images.unsplash.com/photo-1582560475093-ba66accbc424?w=800&auto=format&fit=crop&q=80",
            galleryImageUrls = listOf(
                "https://images.unsplash.com/photo-1582560475093-ba66accbc424?w=800&auto=format&fit=crop&q=80"
            ),
            giTagNumber = "GI Application #183 (Registered)",
            giRegisteredYear = "2014",
            primaryMasterArtisanId = "seller001",
            masterArtisanName = "Savita Dhodi",
            masterArtisanVillage = "Mokhada, Palghar, Maharashtra",
            relatedProductIds = listOf("prod001"),
            verifiedByArtisan = true,
            isFeatured = true,
            viewCount = 3840,
            learnerCount = 920
        ),

        // ── 2. Madhubani / Mithila Painting (Bihar) ─────────────────────────
        CraftHeritageModel(
            craftId = "heritage_madhubani_02",
            craftNameEn = "Mithila Madhubani Folk Art",
            craftNameHi = "मिथिला मधुबनी लोक चित्रकला",
            category = "Folk Painting",
            state = "Bihar",
            district = "Madhubani",
            region = "Mithila Cultural Region",
            community = "Mithila Women Guilds",
            traditionAgeYears = 2200,
            summaryEn = "Ancient narrative art practiced by Mithila women featuring vivid natural pigments, intricate double-line borders, and mythological fertility symbols.",
            summaryHi = "मिथिला की महिलाओं द्वारा पीढ़ियों से संरक्षित कला, जिसमें प्राकृतिक रंगों, दोहरी रूपरेखा और पौराणिक व मांगलिक प्रतीकों का अंकन होता है।",
            storyKahaaniEn = "Tradition holds that King Janaka commissioned the women of Mithila to paint walls and courtyards to celebrate the marriage of his daughter Sita to Lord Rama. The style is celebrated for its distinctive Kachni (fine hatching) and Bharni (vibrant solid filling) styles, with no empty space left on the canvas.",
            storyKahaaniHi = "मान्यता है कि राजा जनक ने सीता-राम विवाह के पावन अवसर पर मिथिला की महिलाओं से पूरे नगर की दीवारों पर चित्रकारी करवाई थी। यह शैली अपनी 'कचनी' (बारीक रेखांकन) और 'भरनी' (रंग भराव) शैलियों के लिए विश्वविख्यात है। इसमें कैनवास पर कोई भी स्थान रिक्त नहीं छोड़ा जाता।",
            culturalSignificance = "Celebration of love, fertility, and cosmological harmony. Central to wedding pavilions (Kohbar Ghar) and auspicious rites.",
            rawMaterials = listOf(
                CraftMaterial(
                    nameEn = "Soot & Lampblack (Kajal)",
                    nameHi = "दीपक की कालिख (काजल)",
                    source = "Mustard oil earthen lamps held under clay pots",
                    isEcoFriendly = true,
                    preparationNote = "Yields permanent velvety black line outlines."
                ),
                CraftMaterial(
                    nameEn = "Turmeric & Palash Yellow",
                    nameHi = "हल्दी व पलाश का पीला रंग",
                    source = "Raw turmeric root extract and dried Palash flowers",
                    isEcoFriendly = true,
                    preparationNote = "Boiled and filtered for luminous sunshine gold."
                ),
                CraftMaterial(
                    nameEn = "Cow Dung Treated Handmade Paper",
                    nameHi = "गोबर शोधित हस्तनिर्मित कागज",
                    source = "Cotton rag paper treated with a light wash of fresh cow dung slurry",
                    isEcoFriendly = true,
                    preparationNote = "Provides natural antiseptic protection and rustic ochre tint."
                )
            ),
            toolsUsed = listOf("Bamboo Nibs", "Cotton Swab Stylus", "Twig Brushes"),
            processSteps = listOf(
                CraftStep(
                    stepNumber = 1,
                    titleEn = "Double-Line Outline (Rekha)",
                    titleHi = "दोहरी रूपरेखा का निर्माण",
                    descriptionEn = "Drawing distinct double outlines using black lampblack ink without prior pencil sketches.",
                    descriptionHi = "बिना किसी पेंसिल रेखाचित्र के, सीधे काजल की स्याही से दोहरी रूपरेखा खींचना।",
                    durationMin = 30
                ),
                CraftStep(
                    stepNumber = 2,
                    titleEn = "Kachni Line Hatching",
                    titleHi = "कचनी — बारीक रेखा भराव",
                    descriptionEn = "Filling negative spaces with delicate cross-hatched lines representing texture.",
                    descriptionHi = "बारीक समानांतर रेखाओं से आकृतियों में जान और आकर्षण भरना।",
                    durationMin = 45
                ),
                CraftStep(
                    stepNumber = 3,
                    titleEn = "Bharni Natural Color Infusion",
                    titleHi = "भरनी — प्राकृतिक रंगों का भराव",
                    descriptionEn = "Pouring vibrant yellow, red, and blue extracted from flowers into the floral and deity motifs.",
                    descriptionHi = "फूलों और पत्तियों से तैयार रंगों को आकृतियों में समृद्धता से भरना।",
                    durationMin = 60
                )
            ),
            motifsAndSymbols = listOf(
                CraftSymbol(
                    symbolNameEn = "Kohbar (Lotus & Bamboo)",
                    symbolNameHi = "कोहबर (कमल व बांस)",
                    meaningEn = "Lotus represents female purity; bamboo stalk represents male lineage and longevity.",
                    meaningHi = "कमल स्त्री तत्व और बांस वंश वृद्धि व दीर्घायु का मांगलिक प्रतीक है।"
                ),
                CraftSymbol(
                    symbolNameEn = "Fish (Matsya)",
                    symbolNameHi = "मत्स्य (मछली)",
                    meaningEn = "Symbol of fertility, prosperity, and life-giving water.",
                    meaningHi = "उर्वरता, समृद्धि और जल के जीवनदायी प्रवाह का प्रतीक।"
                ),
                CraftSymbol(
                    symbolNameEn = "The Peacock (Mayur)",
                    symbolNameHi = "मयूर (मोर)",
                    meaningEn = "Represents romantic love, beauty, and celestial grace.",
                    meaningHi = "प्रेम, सौंदर्य और दिव्य अनुग्रह का प्रतीक।"
                )
            ),
            oralHistoryAudioUrl = "https://actions.google.com/sounds/v1/ambiences/rain_heavy.ogg",
            oralHistoryTranscriptHi = "मिथिला में हम कागज पर नहीं, अपनी अंतरात्मा के उल्लास को उकेरते हैं। हमारी उंगलियों को किसी नाप-तौल की जरूरत नहीं होती; यह हुनर हमें अपनी मां की गोद में खेलते हुए सहज ही मिल गया।",
            oralHistoryTranscriptEn = "In Mithila, we do not paint on paper; we paint the joyful songs of our soul. Our hands do not need rulers or stencils; this sacred discipline was absorbed while playing in our mothers' laps.",
            oralHistoryDurationSec = 170,
            oralNarratorName = "Radha Devi (Mithila Artist Guild)",
            processVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            bannerImageUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=800&auto=format&fit=crop&q=80",
            galleryImageUrls = listOf(
                "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=800&auto=format&fit=crop&q=80"
            ),
            giTagNumber = "GI Application #105 (Registered)",
            giRegisteredYear = "2007",
            primaryMasterArtisanId = "seller002",
            masterArtisanName = "Manju Jha",
            masterArtisanVillage = "Ranti, Madhubani, Bihar",
            relatedProductIds = listOf("prod002"),
            verifiedByArtisan = true,
            isFeatured = true,
            viewCount = 2950,
            learnerCount = 740
        ),

        // ── 3. Jaipur Blue Pottery (Rajasthan) ──────────────────────────────
        CraftHeritageModel(
            craftId = "heritage_blue_pottery_03",
            craftNameEn = "Jaipur Blue Pottery",
            craftNameHi = "जयपुर नीली मृद्भांड कला",
            category = "Terracotta & Ceramic",
            state = "Rajasthan",
            district = "Jaipur",
            region = "Dhundhar Plain",
            community = "Khangarot & Kumbhar Artisans",
            traditionAgeYears = 450,
            summaryEn = "World-famous glazed ceramic craft made entirely without clay, using crushed quartz stone, glass, Fuller's earth, and vibrant cobalt blue glaze.",
            summaryHi = "विश्व प्रसिद्ध चमकदार चीनी मिट्टी कला, जो बिना किसी साधारण मिट्टी के, शुद्ध क्वार्ट्ज पत्थर, कांच, मुल्तानी मिट्टी और कोबाल्ट नीले रंग से बनती है।",
            storyKahaaniEn = "Brought from Turko-Persian traditions to Amber by Raja Man Singh I in the 17th century, Jaipur Blue Pottery flourished under Maharaja Sawai Ram Singh II. Unlike conventional pottery, it does not use clay. The body is formed from dough of quartz powder and glass, fired only once in wood kilns at over 800°C.",
            storyKahaaniHi = "17वीं शताब्दी में राजा मानसिंह प्रथम द्वारा तुर्क-फारसी परम्परा से आमेर लाई गई यह कला सवाई रामसिंह द्वितीय के संरक्षण में फली-फूली। इसमें साधारण मिट्टी का उपयोग नहीं होता, बल्कि क्वार्ट्ज और कांच के पाउडर के मिश्रण से बर्तन बनाकर लकड़ी की भट्ठियों में पकाया जाता है।",
            culturalSignificance = "Royal craftsmanship blending Mughal courtly aesthetics with Rajput royal heritage.",
            rawMaterials = listOf(
                CraftMaterial(
                    nameEn = "Crushed Quartz Stone",
                    nameHi = "क्वार्ट्ज पत्थर का चूर्ण",
                    source = "Natural quartz deposits of Aravalli ranges",
                    isEcoFriendly = true,
                    preparationNote = "Pulverized into micro-fine white sand."
                ),
                CraftMaterial(
                    nameEn = "Natural Cobalt & Copper Oxide",
                    nameHi = "कोबाल्ट व तांबा ऑक्साइड",
                    source = "Mineral oxides mined in Rajasthan",
                    isEcoFriendly = true,
                    preparationNote = "Cobalt produces rich royal navy blue; copper produces brilliant turquoise green."
                ),
                CraftMaterial(
                    nameEn = "Fuller's Earth (Multani Mitti)",
                    nameHi = "मुल्तानी मिट्टी",
                    source = "Natural absorbent bentonite clay",
                    isEcoFriendly = true,
                    preparationNote = "Acts as the cohesive dough binder without shrinking."
                )
            ),
            toolsUsed = listOf("Open Wood Fired Kilns", "Stone Grinding Wheels", "Fine Squirrel Hair Brushes"),
            processSteps = listOf(
                CraftStep(
                    stepNumber = 1,
                    titleEn = "Dough Kneading (Gond Gondhna)",
                    titleHi = "मिश्रण का गूथना",
                    descriptionEn = "Blending quartz powder, crushed glass, Multani Mitti, and natural gum into an elastic dough.",
                    descriptionHi = "क्वार्ट्ज, कांच और मुल्तानी मिट्टी को गोंद के साथ मिलाकर लोचदार लेप बनाना।",
                    durationMin = 40
                ),
                CraftStep(
                    stepNumber = 2,
                    titleEn = "Mould Pressing",
                    titleHi = "सांचों में ढलाई",
                    descriptionEn = "Pressing dough into open terracotta moulds and filling with burnt wood ash for shape retention.",
                    descriptionHi = "सांचों में पेस्ट को दबाकर राख भरना ताकि सूखते समय आकार न बिगड़े।",
                    durationMin = 35
                ),
                CraftStep(
                    stepNumber = 3,
                    titleEn = "Freehand Brush Painting",
                    titleHi = "हस्त आलेखन",
                    descriptionEn = "Painting arabesque floral lattices and peacocks with cobalt oxide using ultra-fine hair brushes.",
                    descriptionHi = "कोबाल्ट ऑक्साइड से बारीक ब्रश द्वारा फारसी और राजस्थानी बेल-बूटों का आलेखन।",
                    durationMin = 50
                ),
                CraftStep(
                    stepNumber = 4,
                    titleEn = "Single Glaze Kiln Firing",
                    titleHi = "भट्ठी में तपन व चमक",
                    descriptionEn = "Coating with borax glass glaze and firing once at 800°C for three days until glossy brilliance appears.",
                    descriptionHi = "कांच के लेप के साथ लकड़ी की भट्ठी में पकाना जिससे कांच जैसी चमक उत्पन्न होती है।",
                    durationMin = 180
                )
            ),
            motifsAndSymbols = listOf(
                CraftSymbol(
                    symbolNameEn = "Arabesque Foliage (Bel-Boote)",
                    symbolNameHi = "बेल-बूटे (लताएं)",
                    meaningEn = "Intertwined vine florals representing endless abundance and natural prosperity.",
                    meaningHi = "लताओं का घुमावदार आलेखन जो अनवरत समृद्धि और प्रकृति के उपहारों का प्रतीक है।"
                ),
                CraftSymbol(
                    symbolNameEn = "Turquoise Lotus Medallion",
                    symbolNameHi = "फ़िरोज़ी कमल चक्र",
                    meaningEn = "Spiritual purity and serenity rising above mundane surroundings.",
                    meaningHi = "सांसारिक व्याधियों से ऊपर उठकर पवित्रता और शांति का प्रतीक।"
                )
            ),
            oralHistoryAudioUrl = "https://actions.google.com/sounds/v1/doors/door_wood_close.ogg",
            oralHistoryTranscriptHi = "नीली मृद्भांड केवल एक बर्तन नहीं, जयपुर के महलों की गूंज है। जब भट्ठी खुलती है और नीले रंग की आभा चमकती है, तो लगता है जैसे नीले आसमान का एक टुकड़ा धरती पर उतर आया हो।",
            oralHistoryTranscriptEn = "Blue pottery is not merely a vessel; it is an echo of Jaipur's royal courtyards. When the kiln cools and that deep cobalt glaze shines through, it feels as though a piece of the azure sky has descended onto Earth.",
            oralHistoryDurationSec = 160,
            oralNarratorName = "Kripal Singh Kripa (Artisan Master)",
            processVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            bannerImageUrl = "https://images.unsplash.com/photo-1610701596007-11502861dcfa?w=800&auto=format&fit=crop&q=80",
            galleryImageUrls = listOf(
                "https://images.unsplash.com/photo-1610701596007-11502861dcfa?w=800&auto=format&fit=crop&q=80"
            ),
            giTagNumber = "GI Application #34 (Registered)",
            giRegisteredYear = "2008",
            primaryMasterArtisanId = "seller003",
            masterArtisanName = "Gopal Saini",
            masterArtisanVillage = "Kot Jewar, Jaipur, Rajasthan",
            relatedProductIds = listOf("prod003"),
            verifiedByArtisan = true,
            isFeatured = true,
            viewCount = 3120,
            learnerCount = 810
        ),

        // ── 4. Kashmir Pashmina Weaving (Jammu & Kashmir) ───────────────────
        CraftHeritageModel(
            craftId = "heritage_pashmina_04",
            craftNameEn = "Kashmir Pashmina Handloom Weaving",
            craftNameHi = "कश्मीरी पश्मीना हथकरघा बुनाई",
            category = "Handloom Textile",
            state = "Jammu and Kashmir",
            district = "Srinagar",
            region = "Kashmir Valley & Ladakh",
            community = "Changpa Nomads & Kashmiri Master Weavers",
            traditionAgeYears = 650,
            summaryEn = "Royal textile spun from the microscopic underfleece of Changthangi mountain goats at 14,000 ft, handwoven on traditional wooden pit looms.",
            summaryHi = "14,000 फीट की ऊंचाई पर पाई जाने वाली चांगथांगी बकरियों के सूक्ष्म रोएं से काता गया शाही वस्त्र, जो पारंपरिक लकड़ी के खड्ड करघे पर बुना जाता है।",
            storyKahaaniEn = "Pashmina was popularized in Kashmir during the 14th century by Sufi saint Mir Sayyid Ali Hamadani. The fibers measure a breathtaking 12 to 15 microns in diameter (six times thinner than human hair). Pure Pashmina is so delicate it can only be hand-spun on the wooden 'Yender' spindle and hand-woven on slow pit looms.",
            storyKahaaniHi = "14वीं सदी में सूफी संत मीर सैयद अली हमदानी ने इस नायाब रेशे की पहचान की। इसका रेशा मानव बाल से छह गुना पतला (12 से 15 माइक्रोन) होता है। इतनी कोमलता के कारण इसे मशीनों पर नहीं बुना जा सकता; इसे लकड़ी के येन्दर (चरखे) पर कातकर हाथों से ही बुना जाता है।",
            culturalSignificance = "Pinnacle of Indian luxury weaving. Embodies the resilience of Himalayan nomadic communities and the meditative patience of Kashmiri weavers.",
            rawMaterials = listOf(
                CraftMaterial(
                    nameEn = "Raw Pashm Cashmere Fleece",
                    nameHi = "कच्चा पश्म रेशा",
                    source = "Naturally shed underbelly fleece of high-altitude Capra Hircus goats in Ladakh",
                    isEcoFriendly = true,
                    preparationNote = "Carefully combed out in spring without harming the animal."
                ),
                CraftMaterial(
                    nameEn = "Natural Saffron & Walnut Dye",
                    nameHi = "केसर व अखरोट की छाल का प्राकृतिक रंग",
                    source = "Pampore saffron and wild Kashmiri walnut husks",
                    isEcoFriendly = true,
                    preparationNote = "Imparts warm earthy golden and deep brown tones."
                )
            ),
            toolsUsed = listOf("Traditional Wooden Yender Spindle", "Handloom Pit Looms", "Bone Weaving Needles"),
            processSteps = listOf(
                CraftStep(
                    stepNumber = 1,
                    titleEn = "Hand Sorting & Dehairing",
                    titleHi = "पश्म की छंटाई",
                    descriptionEn = "Separating coarse guard hair from micro-fine underbelly fleece by hand.",
                    descriptionHi = "हाथों से मोटे बालों को अलग करके केवल रेशमी पश्म रोएं का चयन करना।",
                    durationMin = 60
                ),
                CraftStep(
                    stepNumber = 2,
                    titleEn = "Yender Spindle Spinning",
                    titleHi = "येन्दर चरखे पर कताई",
                    descriptionEn = "Spinning gossamer-thin threads using pure rice powder as a delicate lubricant.",
                    descriptionHi = "चावल के चूर्ण की मदद से चरखे पर अत्यंत महीन सूत कातना।",
                    durationMin = 90
                ),
                CraftStep(
                    stepNumber = 3,
                    titleEn = "Diamond Weave (Chashm-e-Bulbul)",
                    titleHi = "चश्म-ए-बुलबुल बुनाई",
                    descriptionEn = "Hand-interlacing the warp and weft to create the celebrated bulbul eye diamond weave pattern.",
                    descriptionHi = "हथकरघे पर बारीक हीरे जैसी संरचना (बुलबुल की आंख) का ताना-बाना बुनना।",
                    durationMin = 180
                )
            ),
            motifsAndSymbols = listOf(
                CraftSymbol(
                    symbolNameEn = "Kalka Paisley (Boteh)",
                    symbolNameHi = "काल्का बादाम (बूटा)",
                    meaningEn = "Ancient mango/cypress tree motif signifying fertility, vitality, and eternal life.",
                    meaningHi = "आम्र और सनोबर वृक्ष से प्रेरित बूटा, जो जीवन की अमरता और जीवन-शक्ति का प्रतीक है।"
                ),
                CraftSymbol(
                    symbolNameEn = "Chinar Leaf (Chinar Patte)",
                    symbolNameHi = "चिनार का पत्ता",
                    meaningEn = "The majestic emblem of Kashmir Valley, representing changing seasons and enduring resilience.",
                    meaningHi = "कश्मीर का राजसी प्रतीक, जो ऋतुओं के परिवर्तन और अटूट धैर्य को दर्शाता है।"
                )
            ),
            oralHistoryAudioUrl = "https://actions.google.com/sounds/v1/wind/wind_chimes_medium.ogg",
            oralHistoryTranscriptHi = "पश्मीना केवल एक शॉल नहीं है, यह लद्दाख की हाड़ कंपा देने वाली ठंड और कश्मीर के बुनकरों के ध्यान का संगम है। हर धागे में हमारे पुरखों की मेहनत और दुआएं बुनी होती हैं।",
            oralHistoryTranscriptEn = "Pashmina is not just a shawl; it is the meeting point of Ladakh's freezing winds and the meditative prayer of Kashmiri weavers. In every single thread, the endurance and blessings of our ancestors are woven.",
            oralHistoryDurationSec = 210,
            oralNarratorName = "Ghulam Mohammad Zargar",
            processVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            bannerImageUrl = "https://images.unsplash.com/photo-1607344645866-009c320c5ab8?w=800&auto=format&fit=crop&q=80",
            galleryImageUrls = listOf(
                "https://images.unsplash.com/photo-1607344645866-009c320c5ab8?w=800&auto=format&fit=crop&q=80"
            ),
            giTagNumber = "GI Application #46 (Registered)",
            giRegisteredYear = "2008",
            primaryMasterArtisanId = "seller004",
            masterArtisanName = "Bashir Ahmed",
            masterArtisanVillage = "Nowshera, Srinagar, Kashmir",
            relatedProductIds = listOf("prod004"),
            verifiedByArtisan = true,
            isFeatured = true,
            viewCount = 4210,
            learnerCount = 1150
        ),

        // ── 5. Bastar Dhokra Bell Metalcraft (Chhattisgarh) ─────────────────
        CraftHeritageModel(
            craftId = "heritage_dhokra_05",
            craftNameEn = "Bastar Dhokra Lost-Wax Bell Metal",
            craftNameHi = "बस्तर ढोकरा कांस्य शिल्प",
            category = "Metalcraft",
            state = "Chhattisgarh",
            district = "Bastar",
            region = "Bastar Tribal Woodlands",
            community = "Ghadwa & Jhara Tribal Clans",
            traditionAgeYears = 4000,
            summaryEn = "4,000-year-old non-ferrous lost-wax metal casting directly tracing back to the Dancing Girl of Mohenjo-daro, using pure beeswax threads and river clay.",
            summaryHi = "चार हज़ार वर्ष पुरानी मोम-क्षय कांस्य ढलाई कला, जिसका सीधा सम्बंध मोहनजोदड़ो की 'नर्तकी' से है। इसमें शुद्ध मधुमक्खी मोम और नदी की मिट्टी का उपयोग होता है।",
            storyKahaaniEn = "Practiced by the Ghadwa tribes of Bastar, Dhokra is one of humanity's earliest metal casting techniques. The artisan sculpts an intricate armature out of hand-rolled beeswax threads, covers it with refractory clay layers, and bakes it in pit furnaces. When molten brass is poured, the wax melts away ('cire perdue'), leaving behind a one-of-a-kind hollow metal masterpiece that can never be replicated.",
            storyKahaaniHi = "बस्तर की घड़वा जनजाति द्वारा संरक्षित ढोकरा धातु ढलाई की सबसे प्राचीन विधाओं में से है। इसमें पहले मधुमक्खी के मोम की महीन तारें बनाकर आकृति रची जाती है, फिर मिट्टी का लेप लगाकर भट्ठी में तपाया जाता है। पिघला हुआ कांस्य मोम का स्थान ले लेता है। हर ढोकरा मूर्ति अद्वितीय होती है क्योंकि सांचा केवल एक ही बार काम आता है।",
            culturalSignificance = "Living relic of the Bronze Age civilization. Sacred medium for tribal totems, village guardian deities, and harvest rites.",
            rawMaterials = listOf(
                CraftMaterial(
                    nameEn = "Natural Forest Beeswax",
                    nameHi = "जंगली मधुमक्खी का मोम",
                    source = "Collected sustainably from deep Bastar Sal forests",
                    isEcoFriendly = true,
                    preparationNote = "Softened with mustard oil and extruded through wooden presses into thin wax coils."
                ),
                CraftMaterial(
                    nameEn = "Indravati Riverbed Clay & Rice Husk",
                    nameHi = "इंद्रावती नदी की गाद व धान की भूसी",
                    source = "Alluvial river clay mixed with charred rice husk and ant-hill earth",
                    isEcoFriendly = true,
                    preparationNote = "Withstands extreme thermal shock of molten metal."
                ),
                CraftMaterial(
                    nameEn = "Bell Metal / Recycled Brass",
                    nameHi = "कांसा व पीतल मिश्र धातु",
                    source = "Traditional bronze alloy of copper and tin",
                    isEcoFriendly = true,
                    preparationNote = "Produces resonant metallic ring when struck."
                )
            ),
            toolsUsed = listOf("Wood-turned Wax Extruder (Pichki)", "Charcoal Pit Furnaces", "Goat-skin Bellows"),
            processSteps = listOf(
                CraftStep(
                    stepNumber = 1,
                    titleEn = "Clay Core Modelling",
                    titleHi = "मिट्टी की अंतःसंरचना",
                    descriptionEn = "Sculpting the basic core figure with clay and rice husk, allowed to air-dry in shade.",
                    descriptionHi = "धान की भूसी मिली मिट्टी से मूर्ति का बुनियादी ढांचा तैयार करना।",
                    durationMin = 40
                ),
                CraftStep(
                    stepNumber = 2,
                    titleEn = "Wax Thread Embellishment",
                    titleHi = "मोम के तारों से नक्काशी",
                    descriptionEn = "Extruding beeswax threads and winding them meticulously over the core to define facial features and jewellery.",
                    descriptionHi = "मोम की पतली तारों को मिट्टी के ढांचे पर लपेटकर आभूषण और नैन-नक्श गढ़ना।",
                    durationMin = 75
                ),
                CraftStep(
                    stepNumber = 3,
                    titleEn = "Lost-Wax Molten Casting",
                    titleHi = "मोम-क्षय ढलाई",
                    descriptionEn = "Enclosing the wax model in thick clay and pouring red-hot molten brass at 1100°C so wax melts out.",
                    descriptionHi = "मिट्टी के मजबूत खोल में पिघला हुआ कांसा भरना, जिससे मोम पिघलकर बाहर आ जाता है और धातु उसका स्थान ले लेती है।",
                    durationMin = 120
                )
            ),
            motifsAndSymbols = listOf(
                CraftSymbol(
                    symbolNameEn = "Danteshwari Guardian Elephant",
                    symbolNameHi = "दंतेश्वरी गजराज",
                    meaningEn = "Emblem of tribal royalty, protective spiritual power, and forest guardianship.",
                    meaningHi = "जनजातीय राजसी गरिमा और वन-संरक्षक शक्तियों का प्रतीक।"
                ),
                CraftSymbol(
                    symbolNameEn = "Tribal Flute Player & Musicians",
                    symbolNameHi = "जनजातीय वाद्य वादक",
                    meaningEn = "Celebration of tribal joy, music, and eternal communion with forest spirits.",
                    meaningHi = "प्रकृति और वन-देवताओं के साथ उल्लास और संगीत का शाश्वत सम्बंध।"
                )
            ),
            oralHistoryAudioUrl = "https://actions.google.com/sounds/v1/tools/blacksmith_anvil_hammer.ogg",
            oralHistoryTranscriptHi = "हम आग और मिट्टी के लोग हैं। जब हम मोम की पतली तारों को हाथ में लेते हैं, तो लगता है जैसे हम वही कर रहे हैं जो हमारे पुरखे हज़ारों साल पहले नदी किनारे करते थे। ढोकरा की हर मूर्ति एक सांस है, जो दुबारा कभी नहीं दोहराई जा सकती।",
            oralHistoryTranscriptEn = "We are people of fire and clay. When we shape the fine threads of beeswax, we feel connected to what our ancestors did thousands of years ago by the riverbanks. Every Dhokra sculpture is a single breath of life, never to be duplicated.",
            oralHistoryDurationSec = 185,
            oralNarratorName = "Budhram Ghadwa",
            processVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            bannerImageUrl = "https://images.unsplash.com/photo-1544717305-2782549b5136?w=800&auto=format&fit=crop&q=80",
            galleryImageUrls = listOf(
                "https://images.unsplash.com/photo-1544717305-2782549b5136?w=800&auto=format&fit=crop&q=80"
            ),
            giTagNumber = "GI Application #83 (Registered)",
            giRegisteredYear = "2008",
            primaryMasterArtisanId = "seller005",
            masterArtisanName = "Budhram Ghadwa",
            masterArtisanVillage = "Kondagaon, Bastar, Chhattisgarh",
            relatedProductIds = listOf("prod005"),
            verifiedByArtisan = true,
            isFeatured = true,
            viewCount = 3490,
            learnerCount = 890
        )
    )

    fun getCraftById(craftId: String): CraftHeritageModel? {
        return livingTraditions.find { it.craftId == craftId }
    }

    fun getCraftsByState(state: String): List<CraftHeritageModel> {
        return livingTraditions.filter { it.state.equals(state, ignoreCase = true) }
    }

    fun getCraftsByCategory(category: String): List<CraftHeritageModel> {
        if (category == "All" || category.isBlank()) return livingTraditions
        return livingTraditions.filter { it.category.equals(category, ignoreCase = true) }
    }
}
