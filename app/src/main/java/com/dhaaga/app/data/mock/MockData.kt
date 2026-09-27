package com.dhaaga.app.data.mock

import com.dhaaga.app.data.model.CartItemModel
import com.dhaaga.app.data.model.OrderModel
import com.dhaaga.app.data.model.AddressModel
import com.dhaaga.app.data.model.ProductModel
import com.dhaaga.app.data.model.UserModel

/**
 * Mock data for prototype — will be replaced by Firestore reads.
 * All prices in paise (₹1 = 100 paise).
 */
object MockData {

    val indianStates = listOf(
        "Andaman and Nicobar Islands",
        "Andhra Pradesh",
        "Arunachal Pradesh",
        "Assam",
        "Bihar",
        "Chandigarh",
        "Chhattisgarh",
        "Dadra and Nagar Haveli and Daman and Diu",
        "Delhi",
        "Goa",
        "Gujarat",
        "Haryana",
        "Himachal Pradesh",
        "Jammu and Kashmir",
        "Jharkhand",
        "Karnataka",
        "Kerala",
        "Ladakh",
        "Lakshadweep",
        "Madhya Pradesh",
        "Maharashtra",
        "Manipur",
        "Meghalaya",
        "Mizoram",
        "Nagaland",
        "Odisha",
        "Puducherry",
        "Punjab",
        "Rajasthan",
        "Sikkim",
        "Tamil Nadu",
        "Telangana",
        "Tripura",
        "Uttar Pradesh",
        "Uttarakhand",
        "West Bengal"
    )

    val mockSeller = UserModel(
        uid = "seller001",
        phoneNumber = "+919876543210",
        name = "Savita Dhodi",
        role = "seller",
        languagePref = "hi",
        village = "Mokhada",
        district = "Palghar",
        state = "Maharashtra",
        craftTypes = listOf("Warli Art", "Madhubani"),
        shilpiScore = 94,
        bio = "Third-generation Warli artist preserving 2500-year-old sacred tribal folk art in Palghar.",
        yearsExperience = 18,
        lineageGeneration = 3,
        craftHeritageIds = listOf("heritage_warli_01"),
        heritageRecognition = "Master Practitioner • Maharashtra Tribal Heritage Guild",
        oralLoreAudioUrl = "https://actions.google.com/sounds/v1/ambiences/outdoor_market.ogg",
        walletBalance = 240000L,
        totalEarnings = 1850000L
    )

    val mockBuyer = UserModel(
        uid = "buyer001",
        phoneNumber = "+919123456789",
        name = "Rahul Sharma",
        role = "buyer",
        languagePref = "en",
        craftInterests = listOf("Paintings", "Textiles", "Pottery")
    )

    val mockProducts = listOf(
        ProductModel(
            productId = "prod001",
            sellerId = "seller001",
            sellerName = "Savita Dhodi",
            sellerVillage = "Palghar, Maharashtra",
            titleEn = "Warli Sacred Folk Painting — Village Harvest",
            titleHi = "वारली पवित्र लोक चित्र — ग्राम उत्सव",
            descriptionEn = "Authentic hand-painted Warli creation on handmade Geru-washed canvas. Depicts the sacred Tarpa dance and harvest communion with nature.",
            craftType = "Warli Art",
            material = "Natural Rice Paste on Handmade Geru Canvas",
            color = listOf("Earthy Red", "White", "Brown"),
            sizeCm = "30x40 cm",
            technique = "Traditional Warli Stylus Technique",
            region = "Palghar, Maharashtra",
            giTag = "Warli Painting",
            giVerified = true,
            authenticityScore = 96,
            priceListed = 85000L,
            stockQuantity = 5,
            avgRating = 4.9f,
            reviewCount = 28,
            isFeatured = true,
            craftHeritageId = "heritage_warli_01",
            provenanceNote = "Created using fermented rice flour paste applied with bamboo stylus on Geru earth wash.",
            supportBeneficiary = "Direct Patronage: Savita Dhodi & Women Artists Collective, Mokhada",
            imageUrls = listOf(
                "android.resource://com.dhaaga.app/drawable/banner_warli_art"
            ),
            storyEn = "This sacred artwork emerges from the hands of Savita Dhodi, continuing a 2,500-year living tradition of the Warli tribe in Palghar. Every stroke honors Mother Earth."
        ),
        ProductModel(
            productId = "prod002",
            sellerId = "seller002",
            sellerName = "Rekha Kumari",
            sellerVillage = "Madhubani, Bihar",
            titleEn = "Madhubani Peacock Painting",
            titleHi = "मधुबनी मोर चित्र",
            descriptionEn = "Vibrant Madhubani painting featuring peacocks and lotus flowers. Painted with natural vegetable dyes on handmade canvas.",
            craftType = "Madhubani",
            material = "Natural Dyes on Canvas",
            color = listOf("Red", "Yellow", "Blue", "Green"),
            sizeCm = "45x60 cm",
            technique = "Traditional Mithila",
            region = "Madhubani, Bihar",
            giTag = "Madhubani Painting",
            giVerified = true,
            authenticityScore = 91,
            priceListed = 120000L,
            stockQuantity = 3,
            avgRating = 4.9f,
            reviewCount = 41,
            imageUrls = listOf(
                "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_194922_6d6e38dccfb6.jpg",
                "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_194952_3454dd67e172.jpg",
                "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_194920_a2ad5817fc98.jpg",
                "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_194856_3422de9243b3.jpg"
            ),
            craftHeritageId = "heritage_madhubani_02",
            provenanceNote = "Kachni and Bharni strokes painted with natural organic vegetable dyes on handmade canvas.",
            supportBeneficiary = "Rekha Kumari & Mithila Women Artists Guild, Madhubani",
            storyEn = "Rekha Kumari carries forward the 2,200-year Mithila painting tradition, using only natural dyes extracted from flowers and minerals. Each peacock feather is painted individually over 3 days."
        ),
        ProductModel(
            productId = "prod003",
            sellerId = "seller003",
            sellerName = "Gopal Saini",
            sellerVillage = "Kot Jewar, Jaipur, Rajasthan",
            titleEn = "Jaipur Blue Pottery Glazed Tea Set",
            titleHi = "जयपुर ब्लू पॉटरी नक्काशीदार चाय सेट",
            descriptionEn = "Authentic clay-free Jaipur Blue Pottery crafted from crushed quartz stone powder and Fuller's earth, hand-painted with cobalt blue Arabesque florals.",
            craftType = "Blue Pottery",
            material = "Quartz Stone & Glass Glaze with Cobalt Pigment",
            color = listOf("Cobalt Blue", "Turquoise", "White"),
            sizeCm = "Tea cup: 8cm diameter, Kettle: 18cm",
            technique = "Turko-Persian Glazed Quartz Technique",
            region = "Jaipur, Rajasthan",
            giTag = "Blue Pottery of Jaipur",
            giVerified = true,
            authenticityScore = 95,
            priceListed = 199900L,
            stockQuantity = 8,
            avgRating = 4.8f,
            reviewCount = 22,
            craftHeritageId = "heritage_blue_pottery_03",
            provenanceNote = "Crafted entirely without clay using quartz dough and single-fired borax glass glaze.",
            supportBeneficiary = "Gopal Saini Master Ceramic Studio, Kot Jewar Cluster",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1610701596007-11502861dcfa?w=800&auto=format&fit=crop&q=80"
            )
        ),
        ProductModel(
            productId = "prod004",
            sellerId = "seller004",
            sellerName = "Meena Kumawat",
            sellerVillage = "Jaipur, Rajasthan",
            titleEn = "Bandhani Silk Dupatta",
            titleHi = "बंधनी सिल्क दुपट्टा",
            descriptionEn = "Hand-knotted Bandhani silk dupatta in vibrant saffron and pink. 3000+ knots tied by hand before dyeing.",
            craftType = "Bandhani",
            material = "Pure Silk",
            color = listOf("Saffron", "Pink", "Gold"),
            sizeCm = "220x100 cm",
            technique = "Traditional Bandhani Tie-Dye",
            region = "Jaipur, Rajasthan",
            giTag = "Rajasthan Bandhani",
            giVerified = true,
            authenticityScore = 96,
            priceListed = 349900L,
            stockQuantity = 2,
            avgRating = 5.0f,
            reviewCount = 8,
            isFeatured = true,
            imageUrls = listOf(
                "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_190736_996a1d818035.jpg"
            )
        ),
        ProductModel(
            productId = "prod005",
            sellerId = "seller005",
            sellerName = "Budhram Ghadwa",
            sellerVillage = "Kondagaon, Bastar, Chhattisgarh",
            titleEn = "Dhokra Lost-Wax Bell Metal Figurine",
            titleHi = "ढोकरा मोम-क्षय कांस्य जनजातीय मूर्ति",
            descriptionEn = "Ancient lost-wax bell metal casting of a tribal guardian. Hand-coiled beeswax threads cast in molten bronze at 1100°C.",
            craftType = "Dhokra Art",
            material = "Bell Metal Bronze & Forest Beeswax",
            color = listOf("Antique Bronze Gold"),
            sizeCm = "22cm height",
            technique = "4000-Year Lost-Wax Casting (Dhokra)",
            region = "Bastar, Chhattisgarh",
            giTag = "Bastar Dhokra",
            giVerified = true,
            authenticityScore = 98,
            priceListed = 450000L,
            stockQuantity = 4,
            avgRating = 4.9f,
            reviewCount = 19,
            craftHeritageId = "heritage_dhokra_05",
            provenanceNote = "Single-cast hollow bronze created using pure forest beeswax threads and Indravati river clay.",
            supportBeneficiary = "Budhram Ghadwa Tribal Metallurgical Guild, Bastar",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1544717305-2782549b5136?w=800&auto=format&fit=crop&q=80"
            ),
            storyEn = "The Dhokra metalwork tradition traces back over 4,000 years to the Indus Valley Civilization. Budhram Ghadwa carries forward this lost-wax casting technique passed down over countless tribal generations."
        ),
        ProductModel(
            productId = "prod006",
            sellerId = "seller006",
            sellerName = "Fatima Sheikh",
            sellerVillage = "Lucknow, Uttar Pradesh",
            titleEn = "Chikankari Embroidered Kurta",
            titleHi = "चिकनकारी कुर्ता",
            descriptionEn = "Hand-embroidered chikankari kurta on pure cotton fabric. 32 different stitch types used by master karigars of Lucknow.",
            craftType = "Chikankari",
            material = "Pure Cotton with Silk Thread",
            color = listOf("Off-White", "Silver"),
            sizeCm = "Available: S, M, L, XL",
            technique = "Lucknowi Chikankari",
            region = "Lucknow, Uttar Pradesh",
            giTag = "Lucknow Chikankari",
            giVerified = true,
            authenticityScore = 93,
            priceListed = 289900L,
            stockQuantity = 10,
            avgRating = 4.8f,
            reviewCount = 35,
            imageUrls = listOf(
                "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_191026_f2d57092eb8f.jpg"
            )
        ),
        ProductModel(
            productId = "prod007",
            sellerId = "seller007",
            sellerName = "Ghulam Mohammad Zargar",
            sellerVillage = "Downtown Srinagar, Jammu & Kashmir",
            titleEn = "Kashmir Handwoven Pure Pashmina Shawl",
            titleHi = "कश्मीरी हस्तनिर्मित शुद्ध पश्मीना शॉल",
            descriptionEn = "Ultra-fine 14-micron Changthangi mountain goat cashmere fleece, hand-spun on wooden Yender and handwoven on pit looms with Chashm-e-Bulbul diamond weave.",
            craftType = "Pashmina",
            material = "100% Changthang Cashmere Fleece",
            color = listOf("Natural Ivory", "Walnut Dye Trim"),
            sizeCm = "200x100 cm",
            technique = "Handloom Chashm-e-Bulbul Diamond Weave",
            region = "Srinagar, Jammu and Kashmir",
            giTag = "Kashmir Pashmina",
            giVerified = true,
            authenticityScore = 99,
            priceListed = 1850000L,
            stockQuantity = 2,
            avgRating = 5.0f,
            reviewCount = 14,
            isFeatured = true,
            craftHeritageId = "heritage_pashmina_04",
            provenanceNote = "14-micron Changpa fleece spun on wooden Yender spindle and pit-loom woven without power machines.",
            supportBeneficiary = "Ghulam Mohammad Master Weavers Guild, Srinagar",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1607344645866-009c320c5ab8?w=800&auto=format&fit=crop&q=80"
            ),
            storyEn = "A treasure of the Kashmir Valley, hand-spun from Ladakh's high-altitude Changthang plateau goats. Woven by Ghulam Mohammad Zargar with centuries-old meditative patience."
        ),
        ProductModel(
            productId = "prod008",
            sellerId = "seller003",
            sellerName = "Gopal Saini",
            sellerVillage = "Kot Jewar, Jaipur, Rajasthan",
            titleEn = "Handcrafted Ceramic Peacock Vase",
            titleHi = "हस्तनिर्मित मयूर सिरेमिक फूलदान",
            descriptionEn = "Hand-painted Blue Pottery decorative vase featuring royal peacock and lotus motifs with lead-free cobalt glaze.",
            craftType = "Blue Pottery",
            material = "Quartz Stone & Cobalt Glaze",
            color = listOf("Royal Blue", "Turquoise", "White"),
            sizeCm = "25cm height",
            technique = "Jaipur Blue Pottery",
            region = "Jaipur, Rajasthan",
            giTag = "Jaipur Blue Pottery",
            giVerified = true,
            authenticityScore = 95,
            priceListed = 219900L,
            stockQuantity = 4,
            avgRating = 4.9f,
            reviewCount = 22,
            craftHeritageId = "heritage_blue_pottery_03",
            provenanceNote = "100% clay-free quartz stone powder body fired with natural mineral cobalt glazes.",
            supportBeneficiary = "Gopal Saini & Jaipur Potter Guild",
            imageUrls = listOf(
                "android.resource://com.dhaaga.app/drawable/banner_blue_pottery"
            ),
            storyEn = "Crafted by Gopal Saini in Kot Jewar using authentic Turko-Persian glazed quartz techniques passed down since Raja Man Singh I."
        ),
    )

    val categories = listOf(
        "Paintings", "Textiles", "Pottery", "Jewellery",
        "Woodwork", "Leather", "Metal", "Bamboo", "Food", "All"
    )

    val languages = listOf(
        "हिंदी" to "hi",
        "English" to "en",
        "বাংলা" to "bn",
        "தமிழ்" to "ta",
        "తెలుగు" to "te",
        "मराठी" to "mr",
        "ગુજરાતી" to "gu",
        "ಕನ್ನಡ" to "kn",
        "മലയാളം" to "ml",
        "ਪੰਜਾਬੀ" to "pa",
        "ଓଡ଼ିଆ" to "or",
        "اردو" to "ur",
        "অসমীয়া" to "as",
        "संस्कृत" to "sa",
        "Dogri" to "doi",
        "Konkani" to "kok",
        "मैथिली" to "mai",
        "Nepali" to "ne",
        "Santali" to "sat",
        "Manipuri" to "mni",
        "Kashmiri" to "ks",
        "Bhojpuri" to "bho"
    )

    val mockCart = listOf(
        CartItemModel(
            productId = "prod001",
            productTitle = "Warli Tribal Painting",
            productImageUrl = "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_194938_0ec7ce54bf64.jpg",
            sellerName = "Savita Dhodi",
            unitPrice = 85000L,
            quantity = 1
        ),
        CartItemModel(
            productId = "prod003",
            productTitle = "Blue Pottery Tea Set",
            productImageUrl = "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_190732_91ca2ba0e67c.jpg",
            sellerName = "Rajan Patel",
            unitPrice = 199900L,
            quantity = 1
        )
    )

    val mockOrders = listOf(
        OrderModel(
            orderId = "SIH2026ABC123",
            productId = "prod002",
            productTitle = "Madhubani Peacock Painting",
            productImageUrl = "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_194922_6d6e38dccfb6.jpg",
            buyerId = "buyer001",
            buyerName = "Rahul Sharma",
            sellerId = "seller002",
            sellerName = "Rekha Kumari",
            quantity = 1,
            unitPrice = 120000L,
            totalAmount = 130800L,
            status = "shipped",
            paymentMethod = "upi",
            trackingId = "DLH8294756391",
            shippingCarrier = "Delhivery Express",
            deliveryAddress = AddressModel(
                name = "Rahul Sharma",
                line1 = "45, Indiranagar",
                city = "Bengaluru",
                state = "Karnataka",
                pincode = "560038"
            )
        ),
        OrderModel(
            orderId = "SIH2026XYZ456",
            productId = "prod004",
            productTitle = "Bandhani Silk Dupatta",
            productImageUrl = "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_190736_996a1d818035.jpg",
            buyerId = "buyer001",
            buyerName = "Rahul Sharma",
            sellerId = "seller004",
            sellerName = "Meena Kumawat",
            quantity = 1,
            unitPrice = 349900L,
            totalAmount = 381411L,
            status = "delivered",
            paymentMethod = "card",
            trackingId = "DLH5671234890",
            shippingCarrier = "Delhivery Express",
            deliveryAddress = AddressModel(
                name = "Rahul Sharma",
                line1 = "45, Indiranagar",
                city = "Bengaluru",
                state = "Karnataka",
                pincode = "560038"
            )
        )
    )
}
