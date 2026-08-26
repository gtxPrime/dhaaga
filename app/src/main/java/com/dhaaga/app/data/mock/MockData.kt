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
        shilpiScore = 82,
        bio = "Third-generation Warli artist preserving 2500-year-old tribal art.",
        yearsExperience = 15,
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
            titleEn = "Warli Tribal Painting — Village Life",
            titleHi = "वारली जनजातीय चित्र — ग्राम जीवन",
            descriptionEn = "Hand-painted Warli art on handmade paper depicting village harvest festival. 2500-year-old tribal tradition from Palghar, Maharashtra.",
            craftType = "Warli Art",
            material = "Natural Pigments on Handmade Paper",
            color = listOf("Earthy Red", "White", "Brown"),
            sizeCm = "30x40 cm",
            technique = "Traditional Warli",
            region = "Palghar, Maharashtra",
            giTag = "Warli Painting",
            giVerified = true,
            authenticityScore = 94,
            priceListed = 85000L,
            stockQuantity = 5,
            avgRating = 4.8f,
            reviewCount = 23,
            isFeatured = true,
            imageUrls = listOf(
                "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_194938_0ec7ce54bf64.jpg",
                "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_194933_9626290a5d55.jpg",
                "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_194934_fe1e4f881876.jpg",
                "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_194936_274c6a41ae84.jpg"
            ),
            storyEn = "This Warli painting emerges from the hands of Savita Dhodi, a third-generation tribal artist from Palghar. For over 2,500 years, Warli artists have used rice paste on mud walls to document the rhythms of harvest and community."
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
            storyEn = "Rekha Kumari carries forward the 700-year Mithila painting tradition, using only natural dyes extracted from plants and minerals. Each peacock feather is painted individually over 3 days."
        ),
        ProductModel(
            productId = "prod003",
            sellerId = "seller003",
            sellerName = "Rajan Patel",
            sellerVillage = "Khurja, Uttar Pradesh",
            titleEn = "Blue Pottery Tea Set (6 Cups)",
            titleHi = "ब्लू पॉटरी चाय सेट (6 कप)",
            descriptionEn = "Traditional Khurja blue pottery tea set. Hand-painted cobalt blue floral patterns on quartz-glazed clay.",
            craftType = "Blue Pottery",
            material = "Quartz Clay with Cobalt Glaze",
            color = listOf("Blue", "White"),
            sizeCm = "Tea cup: 8cm diameter",
            technique = "Khurja Blue Pottery",
            region = "Khurja, Uttar Pradesh",
            giTag = "Khurja Pottery",
            giVerified = false,
            authenticityScore = 87,
            priceListed = 199900L,
            stockQuantity = 8,
            avgRating = 4.7f,
            reviewCount = 15,
            imageUrls = listOf(
                "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_190732_91ca2ba0e67c.jpg"
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
            sellerName = "Arjun Sahu",
            sellerVillage = "Bastar, Chhattisgarh",
            titleEn = "Dhokra Brass Tribal Figurine",
            titleHi = "ढोकरा पीतल जनजातीय मूर्ति",
            descriptionEn = "Lost-wax cast Dhokra figurine of a tribal woman carrying a pot. 4000-year-old Bastar craft tradition.",
            craftType = "Dhokra Art",
            material = "Bell Metal (Brass)",
            color = listOf("Antique Gold"),
            sizeCm = "18cm height",
            technique = "Lost-Wax Casting (Dhokra)",
            region = "Bastar, Chhattisgarh",
            giTag = "Bastar Dhokra",
            giVerified = true,
            authenticityScore = 98,
            priceListed = 450000L,
            stockQuantity = 4,
            avgRating = 4.9f,
            reviewCount = 19,
            imageUrls = listOf(
                "https://dhaaga.thecoolestportfolio.site/uploads/dhaaga_20260826_190741_7cb753f58f80.jpg"
            ),
            storyEn = "The Dhokra metalwork tradition traces back over 4,000 years to the Indus Valley Civilization. Arjun Sahu is one of 47 remaining master craftsmen who carry forward this lost-wax casting technique."
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
            paymentMethod = "mock_upi",
            trackingId = "DEMO8294756391",
            shippingCarrier = "Delhivery (Demo)",
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
            paymentMethod = "mock_card",
            trackingId = "DEMO5671234890",
            shippingCarrier = "Delhivery (Demo)",
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
