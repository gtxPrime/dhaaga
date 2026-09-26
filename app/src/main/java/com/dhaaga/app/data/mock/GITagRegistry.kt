package com.dhaaga.app.data.mock

data class GITagItem(
    val tagNumber: String,
    val name: String,
    val state: String,
    val region: String,
    val category: String,
    val keywords: List<String>,
    val description: String,
    val registrationYear: String = "2005-2023"
)

object GITagRegistry {

    val OFFICIAL_GI_TAGS: List<GITagItem> = listOf(
        GITagItem(
            tagNumber = "GI-132",
            name = "Banarasi Brocades and Sarees",
            state = "Uttar Pradesh",
            region = "Varanasi Handloom Cluster",
            category = "Textiles & Handlooms",
            keywords = listOf("banarasi", "brocade", "zari", "silk", "saree", "sari", "varanasi", "kashi", "kadwa", "tanchoi", "jangla"),
            description = "Famed fine silk fabric woven with gold and silver brocade (zari) using traditional pit looms."
        ),
        GITagItem(
            tagNumber = "GI-144",
            name = "Kashmiri Pashmina",
            state = "Jammu & Kashmir",
            region = "Srinagar & Changthang Cluster",
            category = "Textiles & Wool",
            keywords = listOf("pashmina", "cashmere", "kashmir", "shawl", "changthangi", "goat wool", "sozni", "needlework"),
            description = "Extremely fine cashmere wool spun and woven by hand, known for feather-light warmth."
        ),
        GITagItem(
            tagNumber = "GI-28",
            name = "Madhubani Paintings (Mithila Art)",
            state = "Bihar",
            region = "Mithila / Madhubani Region",
            category = "Folk Painting & Art",
            keywords = listOf("madhubani", "mithila", "painting", "folk art", "fish", "radha krishna", "natural dyes", "handmade paper"),
            description = "Ancient folk art painted with fingers, twigs, and natural mineral dyes depicting nature and mythology."
        ),
        GITagItem(
            tagNumber = "GI-3",
            name = "Channapatna Toys and Dolls",
            state = "Karnataka",
            region = "Ramanagara District",
            category = "Woodcraft & Toys",
            keywords = listOf("channapatna", "wooden toy", "lacquerware", "wrightia tinctoria", "hale wood", "natural lacquer", "doll"),
            description = "Traditional wooden toys coated with non-toxic vegetable-dyed natural lacquer on rotating lathes."
        ),
        GITagItem(
            tagNumber = "GI-53",
            name = "Bastar Dhokra",
            state = "Chhattisgarh",
            region = "Bastar Tribal Cluster",
            category = "Metal Craft & Lost Wax",
            keywords = listOf("dhokra", "dokra", "bastar", "lost wax", "bell metal", "brass", "tribal craft", "tribal metal"),
            description = "Pre-historic 4,000-year-old lost-wax hollow brass casting technique practiced by tribal artisans."
        ),
        GITagItem(
            tagNumber = "GI-37",
            name = "Blue Pottery of Jaipur",
            state = "Rajasthan",
            region = "Jaipur Cluster",
            category = "Pottery & Ceramics",
            keywords = listOf("blue pottery", "jaipur", "quartz", "ceramic", "cobalt blue", "egyptian paste", "flower vase", "plate"),
            description = "Glazed pottery made without using clay; prepared from ground quartz stone, Fuller's earth, and natural gum."
        ),
        GITagItem(
            tagNumber = "GI-23",
            name = "Kancheepuram Silk",
            state = "Tamil Nadu",
            region = "Kanchipuram Temple Town",
            category = "Textiles & Handlooms",
            keywords = listOf("kancheepuram", "kanjivaram", "silk", "temple border", "pure mulberry", "heavy zari", "korvai"),
            description = "Distinguished heavy mulberry silk saree with contrasting solid woven border created using Korvai interlocking technique."
        ),
        GITagItem(
            tagNumber = "GI-177",
            name = "Sambalpuri Bandha Saree & Fabrics",
            state = "Odisha",
            region = "Sambalpur & Bargarh Cluster",
            category = "Textiles & Handlooms",
            keywords = listOf("sambalpuri", "bandha", "ikat", "tie and dye", "odisha", "cotton saree", "bomkai", "shankha"),
            description = "Intricate double-ikat tie-and-dye weaving featuring traditional conch, wheel, and floral motifs."
        ),
        GITagItem(
            tagNumber = "GI-200",
            name = "Warli Painting",
            state = "Maharashtra",
            region = "Palghar & Thane Tribal Belt",
            category = "Tribal Art",
            keywords = listOf("warli", "tribal art", "tarpa dance", "rice paste", "red ochre", "stick figures", "canvas"),
            description = "Tribal wall art rendered in white rice paste over mud-brick backgrounds using geometric circles, triangles, and squares."
        ),
        GITagItem(
            tagNumber = "GI-44",
            name = "Lucknow Chikan Craft",
            state = "Uttar Pradesh",
            region = "Lucknow Awadh Region",
            category = "Embroidery & Needlecraft",
            keywords = listOf("chikan", "chikankari", "lucknow", "shadow work", "mukaish", "white embroidery", "kurti", "cotton muslin"),
            description = "Delicate and artistic hand embroidery done on translucent muslin, georgette, and organza fabrics."
        ),
        GITagItem(
            tagNumber = "GI-186",
            name = "Pochampally Ikat",
            state = "Telangana",
            region = "Yadadri Bhuvanagiri District",
            category = "Textiles & Handlooms",
            keywords = listOf("pochampally", "ikat", "telangana", "silk saree", "cotton ikat", "geometric patterns", "pagdu bandhu"),
            description = "World-famous geometric tie-and-dye pattern created by dyeing yarns before weaving on fly-shuttle looms."
        ),
        GITagItem(
            tagNumber = "GI-211",
            name = "Odisha Pattachitra",
            state = "Odisha",
            region = "Raghurajpur Heritage Village",
            category = "Folk Painting & Scrolls",
            keywords = listOf("pattachitra", "patachitra", "raghurajpur", "cloth scroll", "jagannath", "tamarind seed gum", "mineral colors"),
            description = "Classical cloth-based scroll painting depicting Lord Jagannath and mythological tales with fine border details."
        ),
        GITagItem(
            tagNumber = "GI-170",
            name = "Phulkari",
            state = "Punjab & Haryana",
            region = "Malwa & Majha Region",
            category = "Embroidery",
            keywords = listOf("phulkari", "punjab", "pat silk", "darning stitch", "flower work", "bagh", "khaddar"),
            description = "Traditional folk floral embroidery using untwisted silk floss (pat) on coarse handwoven cotton khaddar."
        ),
        GITagItem(
            tagNumber = "GI-238",
            name = "Bidriware",
            state = "Karnataka",
            region = "Bidar District",
            category = "Metal Craft & Inlay",
            keywords = listOf("bidriware", "bidri", "silver inlay", "zinc copper alloy", "bidar", "black oxidized", "hookah"),
            description = "Striking black blackened zinc-copper alloy surface inlaid with pure lustrous silver wire sheets."
        ),
        GITagItem(
            tagNumber = "GI-241",
            name = "Kolhapuri Chappal",
            state = "Maharashtra & Karnataka",
            region = "Kolhapur, Sangli, Belagavi",
            category = "Handcrafted Leather",
            keywords = listOf("kolhapuri", "chappal", "leather footwear", "braided leather", "vegetable tanned", "buffalo hide"),
            description = "Handmade vegetable-tanned leather footwear colored with natural plant dyes and assembled without nails."
        ),
        GITagItem(
            tagNumber = "GI-194",
            name = "Kullu Shawl",
            state = "Himachal Pradesh",
            region = "Kullu Valley",
            category = "Woolcraft & Shawls",
            keywords = listOf("kullu", "shawl", "himachal", "geometric border", "angora", "pashmina blend", "tweed"),
            description = "Woolen shawl featuring distinctive geometric bright-colored borders woven with tapestry technique."
        ),
        GITagItem(
            tagNumber = "GI-434",
            name = "Molela Clay Work",
            state = "Rajasthan",
            region = "Nathdwara, Rajsamand",
            category = "Terracotta & Pottery",
            keywords = listOf("molela", "clay plaque", "terracotta", "votive plaque", "donkey dung clay", "devnarayan"),
            description = "Hollow terracotta votive plaques made from clay mixed with donkey manure and sculpted by hand."
        ),
        GITagItem(
            tagNumber = "GI-542",
            name = "Srikalahasti Kalamkari",
            state = "Andhra Pradesh",
            region = "Chittoor District",
            category = "Hand Painted Textiles",
            keywords = listOf("kalamkari", "srikalahasti", "pen craft", "bamboo kalam", "vegetable dyes", "mythological tapestries"),
            description = "Freehand drawing and painting on cotton fabric using a bamboo reed pen (kalam) with natural vegetable dyes."
        ),
        GITagItem(
            tagNumber = "GI-12",
            name = "Mysore Silk",
            state = "Karnataka",
            region = "Mysore Region",
            category = "Textiles & Silk",
            keywords = listOf("mysore silk", "pure silk", "ksic", "gold lace", "crepe silk", "karnataka"),
            description = "Lustrous, pure mulberry crepe silk woven with 100% pure gold and silver zari lace borders."
        ),
        GITagItem(
            tagNumber = "GI-244",
            name = "Thewa Art Work",
            state = "Rajasthan",
            region = "Pratapgarh District",
            category = "Jewellery & Glass Art",
            keywords = listOf("thewa", "gold on glass", "pratapgarh", "rajput art", "fused glass", "23k gold sheet"),
            description = "Exquisite art of fusing intricately engraved 23K gold sheet onto multicolored molten glass base."
        ),
        GITagItem(
            tagNumber = "GI-371",
            name = "Shaphee Lanphee",
            state = "Manipur",
            region = "Imphal Valley",
            category = "Tribal Weaving",
            keywords = listOf("shaphee lanphee", "manipur", "meitei", "black shawl", "red border", "traditional embroidery", "loin loom"),
            description = "Traditional Meitei ceremonial black fabric embroidered with motifs of stars, horses, and fish."
        ),
        GITagItem(
            tagNumber = "GI-71",
            name = "Thanjavur Paintings",
            state = "Tamil Nadu",
            region = "Thanjavur District",
            category = "Classical Art",
            keywords = listOf("thanjavur", "tanjore", "gold leaf", "gemstones", "teak wood board", "gesso work", "sacred icons"),
            description = "Classical South Indian painting characterized by rich, flat, vivid colors, gesso work, and 22K gold foil inlays."
        ),
        GITagItem(
            tagNumber = "GI-182",
            name = "Bhagalpur Silk (Tussar)",
            state = "Bihar",
            region = "Bhagalpur Silk City",
            category = "Textiles & Wild Silk",
            keywords = listOf("bhagalpur", "tussar", "katiya silk", "wild silk", "ghicha", "natural textured silk"),
            description = "Eco-friendly, naturally gold-textured wild Tussar silk spun from cocoons of Antheraea mylitta moths."
        )
    )

    /**
     * Performs fuzzy keyword & regional heuristic matching to detect a GI Tag offline.
     */
    fun findMatchingGiTag(
        title: String,
        description: String,
        state: String,
        category: String
    ): GITagItem? {
        val queryText = "$title $description $state $category".lowercase()

        // 1. Direct name/keyword match
        for (item in OFFICIAL_GI_TAGS) {
            val hasKeywordMatch = item.keywords.any { queryText.contains(it) }
            val hasNameMatch = queryText.contains(item.name.lowercase())
            val hasRegionMatch = queryText.contains(item.region.lowercase()) || queryText.contains(item.state.lowercase())

            if (hasNameMatch || (hasKeywordMatch && hasRegionMatch)) {
                return item
            }
        }

        // 2. Secondary keyword match
        return OFFICIAL_GI_TAGS.firstOrNull { item ->
            val matchCount = item.keywords.count { queryText.contains(it) }
            matchCount >= 2
        }
    }
}
