package com.dhaaga.app.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * Authentic FontAwesome Solid 6.x Icons for Jetpack Compose.
 * Provides crisp, high-definition vector icons across all screen densities.
 */
object FontAwesomeIcons {

    private fun buildIcon(name: String, viewportWidth: Float, viewportHeight: Float, pathData: String): ImageVector {
        val nodes = PathParser().parsePathString(pathData).toNodes()
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight
        ).addPath(
            pathData = nodes,
            fill = SolidColor(Color.Black)
        ).build()
    }

    object Solid {
        val Star: ImageVector by lazy {
            buildIcon(
                name = "fa-star",
                viewportWidth = 576f,
                viewportHeight = 512f,
                pathData = "M316.9 18C311.6 7 300.4 0 288.1 0s-23.4 7-28.8 18L195 150.3 51.4 171.5c-12 1.8-22 10.2-25.7 21.7s-.7 24.2 7.9 32.7L137.8 329 113.2 474.7c-2 12 3 24.2 12.9 31.3s23 8 33.8 2.3l128.3-68.5 128.3 68.5c10.8 5.7 23.9 4.9 33.8-2.3s14.9-19.3 12.9-31.3L438.5 329 542.7 225.9c8.6-8.5 11.7-21.2 7.9-32.7s-13.7-19.9-25.7-21.7L381.2 150.3 316.9 18z"
            )
        }

        val CircleCheck: ImageVector by lazy {
            buildIcon(
                name = "fa-circle-check",
                viewportWidth = 512f,
                viewportHeight = 512f,
                pathData = "M256 512A256 256 0 1 0 256 0a256 256 0 1 0 0 512zM369 209L241 337c-9.4 9.4-24.6 9.4-33.9 0l-64-64c-9.4-9.4-9.4-24.6 0-33.9s24.6-9.4 33.9 0l47 47L335 175c9.4-9.4 24.6-9.4 33.9 0s9.4 24.6 0 33.9z"
            )
        }

        val Check: ImageVector by lazy {
            buildIcon(
                name = "fa-check",
                viewportWidth = 448f,
                viewportHeight = 512f,
                pathData = "M438.6 105.4c12.5 12.5 12.5 32.8 0 45.3l-256 256c-12.5 12.5-32.8 12.5-45.3 0l-128-128c-12.5-12.5-12.5-32.8 0-45.3s32.8-12.5 45.3 0L160 338.7 393.4 105.4c12.5-12.5 32.8-12.5 45.3 0z"
            )
        }

        val WandMagicSparkles: ImageVector by lazy {
            buildIcon(
                name = "fa-wand-magic-sparkles",
                viewportWidth = 512f,
                viewportHeight = 512f,
                pathData = "M384 0l16.8 50.4L451.2 67.2 400.8 84 384 134.4 367.2 84 316.8 67.2 367.2 50.4 384 0zM128 48l11.2 33.6L172.8 92.8 139.2 104 128 137.6 116.8 104 83.2 92.8 116.8 81.6 128 48zM368.5 178.7l50.8 50.8c12.5 12.5 12.5 32.8 0 45.3L167.7 526.4c-12.5 12.5-32.8 12.5-45.3 0L71.6 475.6c-12.5-12.5-12.5-32.8 0-45.3L323.2 178.7c12.5-12.5 32.8-12.5 45.3 0z"
            )
        }

        val Bolt: ImageVector by lazy {
            buildIcon(
                name = "fa-bolt",
                viewportWidth = 384f,
                viewportHeight = 512f,
                pathData = "M0 256L224 0l-48 192h144L96 512l48-224H0z"
            )
        }

        val Camera: ImageVector by lazy {
            buildIcon(
                name = "fa-camera",
                viewportWidth = 512f,
                viewportHeight = 512f,
                pathData = "M149.1 64l-20.8 48H64c-35.3 0-64 28.7-64 64V416c0 35.3 28.7 64 64 64H448c35.3 0 64-28.7 64-64V176c0-35.3-28.7-64-64-64H382.9l-20.8-48H149.1zM256 400a112 112 0 1 1 0-224 112 112 0 1 1 0 224zm0-48a64 64 0 1 0 0-128 64 64 0 1 0 0 128z"
            )
        }

        val Trophy: ImageVector by lazy {
            buildIcon(
                name = "fa-trophy",
                viewportWidth = 576f,
                viewportHeight = 512f,
                pathData = "M400 32H176c-26.5 0-48 21.5-48 48v80c0 70.6 57.4 128 128 128h64c70.6 0 128-57.4 128-128V80c0-26.5-21.5-48-48-48zM96 96H64C28.7 96 0 124.7 0 160v16c0 53 43 96 96 96h32V96zm480 80c0-35.3-28.7-64-64-64h-32v176h32c53 0 96-43 96-96v-16zM320 384h-64v48H160c-17.7 0-32 14.3-32 32s14.3 32 32 32h256c17.7 0 32-14.3 32-32s-14.3-32-32-32h-96v-48z"
            )
        }

        val Tag: ImageVector by lazy {
            buildIcon(
                name = "fa-tag",
                viewportWidth = 448f,
                viewportHeight = 512f,
                pathData = "M0 80V229.5c0 17 6.7 33.3 18.7 45.3l176 176c25 25 65.5 25 90.5 0L414.5 321.5c25-25 25-65.5 0-90.5l-176-176c-12-12-28.3-18.7-45.3-18.7H48C21.5 36 0 57.5 0 84v-4zm112 48a32 32 0 1 1 0-64 32 32 0 1 1 0 64z"
            )
        }

        val Cloud: ImageVector by lazy {
            buildIcon(
                name = "fa-cloud",
                viewportWidth = 640f,
                viewportHeight = 512f,
                pathData = "M0 336c0-79.5 64.5-144 144-144 5.3 0 10.5.3 15.6.8C183.5 123.6 244.6 72 320 72c84.2 0 153.9 65.4 160.7 148.4C535.1 228.6 576 274.6 576 330.7 576 397 522.3 451 456 451H144C64.5 451 0 386.5 0 336z"
            )
        }

        val Store: ImageVector by lazy {
            buildIcon(
                name = "fa-store",
                viewportWidth = 576f,
                viewportHeight = 512f,
                pathData = "M544 192l-32-128H64L32 192v64h32v192c0 17.7 14.3 32 32 32h352c17.7 0 32-14.3 32-32V256h32v-64h32zM128 416V256h288v160H128z"
            )
        }

        val BagShopping: ImageVector by lazy {
            buildIcon(
                name = "fa-bag-shopping",
                viewportWidth = 448f,
                viewportHeight = 512f,
                pathData = "M160 112c0-35.3 28.7-64 64-64s64 28.7 64 64v32H160v-32zm-48 32H48c-26.5 0-48 21.5-48 48v224c0 35.3 28.7 64 64 64h320c35.3 0 64-28.7 64-64V192c0-26.5-21.5-48-48-48h-64v-32C336 49.3 286.7 0 224 0S112 49.3 112 112v32zm-32 80c0-8.8 7.2-16 16-16s16 7.2 16 16v16c0 8.8-7.2 16-16 16s-16-7.2-16-16v-16zm256 0c0-8.8 7.2-16 16-16s16 7.2 16 16v16c0 8.8-7.2 16-16 16s-16-7.2-16-16v-16z"
            )
        }

        val Heart: ImageVector by lazy {
            buildIcon(
                name = "fa-heart",
                viewportWidth = 512f,
                viewportHeight = 512f,
                pathData = "M47.6 300.4L228.3 469.1c7.5 7 17.4 10.9 27.7 10.9s20.2-3.9 27.7-10.9L464.4 300.4c30.4-28.3 47.6-68 47.6-109.5v-5.8c0-69.9-50.5-129.5-119.4-141C347 36.5 300.6 51.4 268 84L256 96 244 84c-32.6-32.6-79-47.5-124.6-39.9C50.5 55.6 0 115.2 0 185.1v5.8c0 41.5 17.2 81.2 47.6 109.5z"
            )
        }

        val MagnifyingGlass: ImageVector by lazy {
            buildIcon(
                name = "fa-magnifying-glass",
                viewportWidth = 512f,
                viewportHeight = 512f,
                pathData = "M416 208c0 45.9-14.9 88.3-40 122.7L502.6 457.4c12.5 12.5 12.5 32.8 0 45.3s-32.8 12.5-45.3 0L330.7 376c-34.4 25.2-76.8 40-122.7 40C93.1 416 0 322.9 0 208S93.1 0 208 0s208 93.1 208 208zM208 352a144 144 0 1 0 0-288 144 144 0 1 0 0 288z"
            )
        }

        val Microphone: ImageVector by lazy {
            buildIcon(
                name = "fa-microphone",
                viewportWidth = 384f,
                viewportHeight = 512f,
                pathData = "M192 0c-44.2 0-80 35.8-80 80V240c0 44.2 35.8 80 80 80s80-35.8 80-80V80c0-44.2-35.8-80-80-80zm128 176c0-8.8-7.2-16-16-16s-16 7.2-16 16v64c0 53-43 96-96 96s-96-43-96-96V176c0-8.8-7.2-16-16-16s-16 7.2-16 16v64c0 65.6 48.4 120 112 127.1V448H160c-8.8 0-16 7.2-16 16s7.2 16 16 16h64 64c8.8 0 16-7.2 16-16s-7.2-16-16-16H224V367.1c63.6-7.1 112-61.5 112-127.1V176z"
            )
        }

        val User: ImageVector by lazy {
            buildIcon(
                name = "fa-user",
                viewportWidth = 448f,
                viewportHeight = 512f,
                pathData = "M224 256A128 128 0 1 0 224 0a128 128 0 1 0 0 256zm-45.7 48C79.8 304 0 383.8 0 482.3 0 498.7 13.3 512 29.7 512H418.3c16.4 0 29.7-13.3 29.7-29.7 0-98.5-79.8-178.3-178.3-178.3h-91.4z"
            )
        }

        val ShieldCheck: ImageVector by lazy {
            buildIcon(
                name = "fa-shield-check",
                viewportWidth = 512f,
                viewportHeight = 512f,
                pathData = "M256 0c4.8 0 9.3 1.6 12.9 4.5l192 144C473.4 158 480 173.9 480 192v96c0 148.9-97.8 280.9-224 320C129.8 568.9 32 436.9 32 288V192c0-18.1 6.6-34 29.1-43.5l192-144C246.7 1.6 251.2 0 256 0zm115.3 211.3l-128 128c-6.2 6.2-16.4 6.2-22.6 0l-64-64c-6.2-6.2-6.2-16.4 0-22.6s16.4-6.2 22.6 0L232 305.4l116.7-116.7c6.2-6.2 16.4-6.2 22.6 0s6.2 16.4 0 22.6z"
            )
        }
    }

    object Brands {
        val Google: ImageVector by lazy {
            buildIcon(
                name = "fa-google",
                viewportWidth = 488f,
                viewportHeight = 512f,
                pathData = "M488 261.8C488 403.3 391.1 504 248 504 110.8 504 0 393.2 0 256S110.8 8 248 8c66.8 0 123 24.5 166.3 64.9l-67.5 64.9C258.5 52.6 94.3 116.6 94.3 256c0 86.5 69.1 156.6 153.7 156.6 98.2 0 135-70.4 140.8-106.9H248v-85.3h236.1c2.3 12.7 3.9 24.9 3.9 41.4z"
            )
        }
    }
}

