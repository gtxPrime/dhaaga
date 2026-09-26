import os
import sys
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
)
from reportlab.pdfgen import canvas

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_number(num_pages)
            super().showPage()
        super().save()

    def draw_page_number(self, page_count):
        if self._pageNumber == 1:
            return  # Suppress page number on cover page
        self.saveState()
        self.setFont("Helvetica", 9)
        self.setFillColor(colors.HexColor("#718096"))
        
        # Header line & text
        self.setStrokeColor(colors.HexColor("#E2E8F0"))
        self.setLineWidth(0.75)
        self.line(40, letter[1] - 35, letter[0] - 40, letter[1] - 35)
        self.drawString(40, letter[1] - 30, "DHAAGA (धागा) — Comprehensive Technical Whitepaper & Team Engineering Matrix")
        self.drawRightString(letter[0] - 40, letter[1] - 30, "IIT Madras Innovation")

        # Footer line & text
        self.line(40, 42, letter[0] - 40, 42)
        self.drawString(40, 30, "Confidential — For Internal Engineering & Leadership Distribution")
        page_str = f"Page {self._pageNumber} of {page_count}"
        self.drawRightString(letter[0] - 40, 30, page_str)
        self.restoreState()

def build_pdf(filename="Dhaaga_Master_Technical_Report.pdf"):
    doc = SimpleDocTemplate(
        filename,
        pagesize=letter,
        leftMargin=40,
        rightMargin=40,
        topMargin=50,
        bottomMargin=50
    )

    styles = getSampleStyleSheet()

    # Custom palette
    c_primary = colors.HexColor("#2B4C26")      # Deep Forest
    c_secondary = colors.HexColor("#537346")    # Sage Green
    c_accent = colors.HexColor("#BD5B38")       # Terra Cotta
    c_dark = colors.HexColor("#1A202C")         # Charcoal Dark
    c_light = colors.HexColor("#F7FAFC")        # Warm White / Light Gray
    c_border = colors.HexColor("#CBD5E1")       # Border Gray

    # Style modifications & additions
    title_style = ParagraphStyle(
        'CoverTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=28,
        leading=34,
        textColor=c_primary,
        alignment=0,
        spaceAfter=8
    )

    subtitle_style = ParagraphStyle(
        'CoverSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=13,
        leading=18,
        textColor=c_secondary,
        alignment=0,
        spaceAfter=15
    )

    h1_style = ParagraphStyle(
        'Heading1_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=18,
        leading=22,
        textColor=c_primary,
        spaceBefore=16,
        spaceAfter=10,
        keepWithNext=True
    )

    h2_style = ParagraphStyle(
        'Heading2_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=13,
        leading=17,
        textColor=c_accent,
        spaceBefore=12,
        spaceAfter=6,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'Body_Custom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=14,
        textColor=c_dark,
        spaceAfter=8
    )

    bullet_style = ParagraphStyle(
        'Bullet_Custom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9,
        leading=13.5,
        textColor=c_dark,
        leftIndent=15,
        spaceAfter=4
    )

    role_badge_style = ParagraphStyle(
        'RoleBadge',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10,
        leading=13,
        textColor=colors.white,
        alignment=1
    )

    table_header = ParagraphStyle(
        'TableHeader',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=9,
        leading=12,
        textColor=colors.white,
        alignment=0
    )

    table_cell = ParagraphStyle(
        'TableCell',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.5,
        leading=11.5,
        textColor=c_dark
    )

    code_style = ParagraphStyle(
        'CodeStyle',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=8,
        leading=10.5,
        textColor=colors.HexColor("#2D3748")
    )

    story = []

    # ══════════════════════════════════════════════════════════════════════════
    # COVER / HEADER SECTION
    # ══════════════════════════════════════════════════════════════════════════
    story.append(Paragraph("DHAAGA (धागा)", title_style))
    story.append(Paragraph("<b>Building the Digital Bridge for Every Indian Artisan</b><br/>Comprehensive System Architecture, Technical Deep-Dive & 6-Discipline Engineering Allocation", subtitle_style))
    
    meta_table_data = [
        [
            Paragraph("<b>Affiliation:</b> Indian Institute of Technology Madras (IIT Madras)", table_cell),
            Paragraph("<b>Platform:</b> Android (Kotlin + Jetpack Compose)", table_cell)
        ],
        [
            Paragraph("<b>Lead Architect & Main Dev:</b> Garvit Sharma (Full-Stack)", table_cell),
            Paragraph("<b>Cloud & AI Stack:</b> Cloud Firestore + Gemini 2.5 Flash + MagicHour", table_cell)
        ],
        [
            Paragraph("<b>Version:</b> 1.4.0 Production Ready (Debug APK Assembled)", table_cell),
            Paragraph("<b>Date:</b> September 2026", table_cell)
        ]
    ]
    t_meta = Table(meta_table_data, colWidths=[270, 260])
    t_meta.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#F0F4EC")),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor("#D2E0C8")),
        ('INNERGRID', (0,0), (-1,-1), 0.5, colors.HexColor("#E2EAD9")),
        ('TOPPADDING', (0,0), (-1,-1), 6),
        ('BOTTOMPADDING', (0,0), (-1,-1), 6),
        ('LEFTPADDING', (0,0), (-1,-1), 10),
        ('RIGHTPADDING', (0,0), (-1,-1), 10),
    ]))
    story.append(t_meta)
    story.append(Spacer(1, 14))

    # Executive Overview
    story.append(Paragraph("Executive Architectural Overview", h1_style))
    story.append(Paragraph(
        "<b>Dhaaga</b> is an enterprise-grade mobile commerce and creative intelligence platform designed to eliminate the predatory 40–70% middleman margins exploiting India's 200+ million rural and tribal artisans. Dhaaga directly links master artisans (producing GI-tagged handlooms, woodwork, brass castings, pottery, and tribal artworks) with urban domestic and international buyers. It achieves this through a revolutionary <b>Voice-to-Catalog AI engine</b>, <b>Automated Studio Quality Photography</b>, <b>Dynamic Margin Protection Pricing</b>, and <b>22 Official Eighth Schedule Indian Language localization</b>.",
        body_style
    ))
    story.append(Paragraph(
        "The codebase is engineered strictly under Android Modern App Architecture (MVVM + Jetpack Compose + Clean Architecture Principles), powered by real-time bi-directional synchronization with Google Cloud Firestore, Firebase Authentication, on-device audio recognition, and simulated UPI/Banking integration.",
        body_style
    ))

    # ══════════════════════════════════════════════════════════════════════════
    # TEAM DIVISION MATRIX (THE 6 PILLARS)
    # ══════════════════════════════════════════════════════════════════════════
    story.append(Spacer(1, 10))
    story.append(Paragraph("Team Structure & Core Disciplines Matrix", h1_style))
    story.append(Paragraph(
        "The project is structured around 6 specialized functional domains, ensuring end-to-end accountability across the product lifecycle:",
        body_style
    ))

    team_summary_data = [
        [
            Paragraph("Team Member", table_header),
            Paragraph("Role & Badge", table_header),
            Paragraph("Primary Domain Ownership", table_header),
            Paragraph("Key Codebase / Assets Mapped", table_header)
        ],
        [
            Paragraph("<b>Garvit Sharma</b>", table_cell),
            Paragraph("<font color='#2B4C26'><b>[FULL STACK]</b><br/>Lead Developer</font>", table_cell),
            Paragraph("App Architecture, MVVM Core, StateFlow reactive pipelines, offline-first sync, Firestore integration, Mock Payment Gateway, build system.", table_cell),
            Paragraph("<code>AppViewModel.kt<br/>MainActivity.kt<br/>CartScreen.kt<br/>MockPaymentDialog.kt</code>", code_style)
        ],
        [
            Paragraph("<b>Sargam Sharma</b>", table_cell),
            Paragraph("<font color='#BD5B38'><b>[UI/UX DESIGN]</b><br/>Team Lead</font>", table_cell),
            Paragraph("Design system (Forest Sage & Terra Cotta), Lore floating navigation bar, Notion avatar customizer, accessibility for rural artisans, micro-animations.", table_cell),
            Paragraph("<code>HomeScreen.kt<br/>HomeTabContents.kt<br/>Color.kt, Theme.kt<br/>FontAwesomeIcons.kt</code>", code_style)
        ],
        [
            Paragraph("<b>Siddhi Rai</b>", table_cell),
            Paragraph("<font color='#00897B'><b>[AI / ML]</b><br/>AI/ML Engineer</font>", table_cell),
            Paragraph("Multilingual Voice Auto-Cataloger, Gemini 2.5 Flash structured prompt engine, MagicHour AI Studio image enhancement, dynamic pricing elasticity model.", table_cell),
            Paragraph("<code>GeminiAIService.kt<br/>VoiceCatalogerDialog.kt<br/>AIStudioDialog.kt<br/>ImageSegmentationHelper.kt</code>", code_style)
        ],
        [
            Paragraph("<b>Ritesh Sah</b>", table_cell),
            Paragraph("<font color='#546E7A'><b>[BACKEND]</b><br/>Backend Engineer</font>", table_cell),
            Paragraph("Firebase Cloud Firestore schema, Firestore Security Rules (RBAC), Firebase Auth (Phone OTP + Google SSO), cloud functions, webhook handlers.", table_cell),
            Paragraph("<code>OrderModel.kt, ProductModel.kt<br/>UserModel.kt<br/>firestore.rules<br/>Cloud Functions TS</code>", code_style)
        ],
        [
            Paragraph("<b>Dipanshu Gola</b>", table_cell),
            Paragraph("<font color='#E65100'><b>[DATA ANALYSIS]</b><br/>Analytics Lead</font>", table_cell),
            Paragraph("Artisan economics telemetry, monthly GMV & payout aggregation, Crashlytics event tracking, funnel drop-off analytics, regional craft demand heatmaps.", table_cell),
            Paragraph("<code>SellerDashboardScreen.kt<br/>DynamicPricingCard.kt<br/>Firebase Analytics Events</code>", code_style)
        ],
        [
            Paragraph("<b>Shashwat Upadhyay</b>", table_cell),
            Paragraph("<font color='#FB8C00'><b>[RESEARCH]</b><br/>Research & Doc</font>", table_cell),
            Paragraph("GI Tag heritage craft database, rural artisan user research, ONDC seller protocol compliance, PM Vishwakarma alignment, 22-language translation audits.", table_cell),
            Paragraph("<code>AppLanguageManager.kt<br/>MockData.kt (States/Crafts)<br/>GI Registry Database</code>", code_style)
        ]
    ]

    t_team = Table(team_summary_data, colWidths=[80, 85, 205, 160])
    t_team.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), c_primary),
        ('BOX', (0,0), (-1,-1), 1, c_border),
        ('INNERGRID', (0,0), (-1,-1), 0.5, c_border),
        ('TOPPADDING', (0,0), (-1,-1), 5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 5),
        ('LEFTPADDING', (0,0), (-1,-1), 6),
        ('RIGHTPADDING', (0,0), (-1,-1), 6),
    ]))
    story.append(t_team)

    story.append(PageBreak())

    # ══════════════════════════════════════════════════════════════════════════
    # DETAILED DOMAIN BREAKDOWN FOR EACH MEMBER
    # ══════════════════════════════════════════════════════════════════════════

    # ──────────────────────────────────────────────────────────────────────────
    # 1. GARVIT SHARMA (FULL-STACK ARCHITECTURE)
    # ──────────────────────────────────────────────────────────────────────────
    story.append(Paragraph("1. Garvit Sharma — Full-Stack Development & Architecture Lead", h1_style))
    story.append(Paragraph("<b>Primary Ownership:</b> AppViewModel.kt, MainActivity.kt, CartScreen.kt, MockPaymentDialog.kt, Reactive Pipelines, Build System", h2_style))
    
    story.append(Paragraph("<b>Current Implementation Deep-Dive:</b>", body_style))
    story.append(Paragraph("• <b>AppViewModel (Central State Hub):</b> Manages 28 distinct StateFlow and MutableStateFlow streams that decouple UI rendering from data persistence. Implements optimistic UI updates for instantaneous responsiveness (e.g. cart badge increment, stock deduction, order confirmed).", bullet_style))
    story.append(Paragraph("• <b>Cloud Synchronization Pipeline:</b> Implements SnapshotListeners on Firestore collections (<code>products</code>, <code>orders</code>, <code>users</code>) alongside on-device SharedPreferences serialization. When an artisan adds a craft or updates stock, changes persist locally and immediately push to Firestore.", bullet_style))
    story.append(Paragraph("• <b>Integrated Mock Payment Engine:</b> Designed and deployed <code>MockPaymentDialog.kt</code> featuring animated multi-phase banking authorization (UPI apps: GPay, PhonePe, Paytm, BHIM; Cards; NetBanking; COD) that records transaction IDs and sets <code>isMockPayment = true</code>.", bullet_style))
    story.append(Paragraph("• <b>Dual-Role Lifecycle & Unified Auth:</b> Resolved dual-account logout edge cases by orchestrating atomic sign-out across Firebase Auth, GoogleSignInClient, listener detachment, and local credential wipes.", bullet_style))

    story.append(Spacer(1, 4))
    story.append(Paragraph("<b>Technical Gaps, Improvements & Roadmap for Garvit:</b>", body_style))
    story.append(Paragraph("1. <b>Room Database with Offline Mutation Queue:</b> Currently, local caching uses SharedPreferences JSON strings. Transition to SQLite/Room with an offline mutation queue (WorkManager) that replays queued product uploads and orders when network reconnects.", bullet_style))
    story.append(Paragraph("2. <b>Dependency Injection (Dagger Hilt):</b> Migrate from manual ViewModel instantiation in MainActivity to Hilt <code>@HiltViewModel</code> and <code>@Inject</code> constructors to improve modularity and unit testability.", bullet_style))
    story.append(Paragraph("3. <b>Paging 3 Integration:</b> As the marketplace grows past 1,000+ crafts, replace direct Firestore list snapshots with <code>PagingSource</code> and <code>LazyPagingItems</code> to ensure 60fps scrolling and reduce Firebase read quotas.", bullet_style))
    story.append(Paragraph("4. <b>Biometric Authentication:</b> Integrate AndroidX <code>BiometricPrompt</code> so artisans can authenticate merchant payouts and banking changes with fingerprint/face unlock instead of passwords.", bullet_style))

    story.append(Spacer(1, 10))

    # ──────────────────────────────────────────────────────────────────────────
    # 2. SARGAM SHARMA (UI/UX DESIGN & DESIGN SYSTEMS)
    # ──────────────────────────────────────────────────────────────────────────
    story.append(Paragraph("2. Sargam Sharma — Team Lead • UI/UX Design", h1_style))
    story.append(Paragraph("<b>Primary Ownership:</b> Visual Identity, Design Tokens, Navigation Shell, Layout Polish, Responsive Accessibility", h2_style))

    story.append(Paragraph("<b>Current Implementation Deep-Dive:</b>", body_style))
    story.append(Paragraph("• <b>Artisan Earth & Forest Palette:</b> Custom-curated tokens: <code>PaletteForest (#60734E)</code>, <code>PaletteSage (#738861)</code>, <code>PaletteGreenTint (#E2EAD9)</code>, and <code>DhaagaPrimary (#8D5B4C)</code>. Represents natural botanical dyes, khadi cotton, and terracotta clay.", bullet_style))
    story.append(Paragraph("• <b>Lore-Exact Floating Pill Navigation:</b> Floating navigation container elevated at 14.dp with custom spring physics (damping ratio 0.52f, stiffness 420f). Center protruding FAB for Shopping Bag / Add Craft with pulsating glowing ripple feedback.", bullet_style))
    story.append(Paragraph("• <b>Dual Mode Persona Navigation:</b> Seamlessly toggles between Buyer (Explore, Wishlist, Bag, My Orders, Profile) and Artisan Seller (Storefront, My Listings, Add Craft, Dashboard, Settings).", bullet_style))
    story.append(Paragraph("• <b>Notion-Style Avatar Engine:</b> Dynamic programmatic SVG avatars generated based on user initials or cloud photo URLs with long-press developer interaction.", bullet_style))

    story.append(Spacer(1, 4))
    story.append(Paragraph("<b>Technical Gaps, Improvements & Roadmap for Sargam:</b>", body_style))
    story.append(Paragraph("1. <b>Low-Literacy UI Audit (Voice-First & Visual Cues):</b> Many master weavers have limited formal literacy. Expand beyond text labels by adding animated visual tooltips and voice prompts across all action cards.", bullet_style))
    story.append(Paragraph("2. <b>High-Sunlight Outdoor Contrast Mode:</b> Artisans frequently photograph and catalog crafts in open-air courtyards or handloom sheds. Add a high-contrast display mode with luminance ratio > 7:1 (WCAG AAA).", bullet_style))
    story.append(Paragraph("3. <b>Lottie Micro-Interactions:</b> Replace basic loading spinners with cultural micro-animations: a spinning traditional charkha or weaving shuttle during AI Studio image processing.", bullet_style))
    story.append(Paragraph("4. <b>Dynamic Font Scaling & RTL Support:</b> Ensure zero-overflow layout guarantees when users increase system accessibility font sizes up to 130% across Indian regional scripts (Devanagari, Tamil, Telugu, Gurmukhi).", bullet_style))

    story.append(PageBreak())

    # ──────────────────────────────────────────────────────────────────────────
    # 3. SIDDHI RAI (AI / ML ENGINEERING)
    # ──────────────────────────────────────────────────────────────────────────
    story.append(Paragraph("3. Siddhi Rai — AI / ML Engineering", h1_style))
    story.append(Paragraph("<b>Primary Ownership:</b> GeminiAIService.kt, VoiceCatalogerDialog.kt, AIStudioDialog.kt, ImageSegmentationHelper.kt, Pricing Elasticity", h2_style))

    story.append(Paragraph("<b>Current Implementation Deep-Dive:</b>", body_style))
    story.append(Paragraph("• <b>Multilingual Voice Auto-Cataloger:</b> Captures speech via Android SpeechRecognizer across 7 primary Indian language models. Audio text is piped into <code>GeminiAIService.autoCatalogProduct(...)</code> with custom prompt engineering enforcing a structured JSON response (Title EN/HI, Story, Category, Dimensions, Price, Tags, Care).", bullet_style))
    story.append(Paragraph("• <b>Dual-Engine AI Studio Photography:</b> Incorporates a fallback image-segmentation algorithm (Euclidean RGB distance thresholding with radial center falloff) alongside cloud AI Studio prompts that format raw photos into professional studio lighting, marble/wood pedestals, and neutral backdrops.", bullet_style))
    story.append(Paragraph("• <b>Dynamic Pricing Intelligence:</b> Analyzes craft category, artisan hours invested, and raw material inputs to generate fair-market retail recommendations and wholesale B2B tiered discounts.", bullet_style))
    story.append(Paragraph("• <b>Sanitized Security Layer:</b> Completely masks internal model names ('Gemini', 'Banana') from end users, presenting client-facing branding: <i>'Fast Gen Intelligence'</i> and <i>'Studio Quality Engine'</i>.", bullet_style))

    story.append(Spacer(1, 4))
    story.append(Paragraph("<b>Technical Gaps, Improvements & Roadmap for Siddhi:</b>", body_style))
    story.append(Paragraph("1. <b>On-Device Gemini Nano / AICore Acceleration:</b> Integrate MediaPipe LLM Inference or Google AICore to run catalog generation directly on device when internet connectivity is spotty in remote artisan villages.", bullet_style))
    story.append(Paragraph("2. <b>Computer Vision Craft Verification:</b> Train a lightweight classification model to verify handmade authenticity (e.g. detecting subtle weave variations in Handloom Pashmina vs machine-woven synthetic replicas).", bullet_style))
    story.append(Paragraph("3. <b>Text-to-Speech (TTS) Catalog Review:</b> Add speech synthesis in the artisan's mother tongue (Hindi, Bengali, Tamil, etc.) that reads aloud the generated title and price so artisans can verify before listing without reading.", bullet_style))
    story.append(Paragraph("4. <b>Dynamic Background Inpainting:</b> Upgrade <code>ImageSegmentationHelper</code> to use MLKit Selfie/Subject Segmentation for crisp alpha-channel extraction and automated background replacement.", bullet_style))

    story.append(Spacer(1, 10))

    # ──────────────────────────────────────────────────────────────────────────
    # 4. RITESH SAH (BACKEND & CLOUD INFRASTRUCTURE)
    # ──────────────────────────────────────────────────────────────────────────
    story.append(Paragraph("4. Ritesh Sah — Backend Engineering", h1_style))
    story.append(Paragraph("<b>Primary Ownership:</b> Cloud Firestore Schema, Security Rules, Firebase Auth, Cloud Functions, Logistics Webhooks", h2_style))

    story.append(Paragraph("<b>Current Implementation Deep-Dive:</b>", body_style))
    story.append(Paragraph("• <b>Firestore Database Architecture:</b> Structured NoSQL schema with root collections: <code>users/{uid}</code>, <code>products/{productId}</code>, and <code>orders/{orderId}</code>. Orders store snapshot data (unit price, platform fee 8%, seller payout 92%, delivery address, tracking ID).", bullet_style))
    story.append(Paragraph("• <b>Authentication Flow:</b> Implements Google Sign-In with credential exchange and Phone OTP verification (Twilio/Firebase Auth SMS) that maps phone numbers to artisan profiles.", bullet_style))
    story.append(Paragraph("• <b>Logistics Tracking Integration:</b> Auto-generates Delhivery Express tracking waybills (DLHXXXXXXXXXX) with step-by-step shipment lifecycle tracking.", bullet_style))

    story.append(Spacer(1, 4))
    story.append(Paragraph("<b>Technical Gaps, Improvements & Roadmap for Ritesh:</b>", body_style))
    story.append(Paragraph("1. <b>Hardened Firestore Security Rules:</b> Deploy granular rules checking <code>request.auth.uid</code>. Ensure buyers cannot modify product prices or seller payout fields, and prevent unauthorized cancellations.", bullet_style))
    story.append(Paragraph("2. <b>Production Escrow & UPI Payout Split (Razorpay Route):</b> Replace simulated mock payments with Razorpay Route / Cashfree. Automatically hold funds in escrow until courier delivery confirmation, then disburse 92% directly to the artisan's UPI VPA / bank account.", bullet_style))
    story.append(Paragraph("3. <b>Cloud Functions for Inventory Locks:</b> Implement Firestore transactions via Cloud Functions to prevent race conditions when multiple buyers purchase the last unique handmade item simultaneously.", bullet_style))
    story.append(Paragraph("4. <b>ONDC (Open Network for Digital Commerce) Seller Adapter:</b> Build Beckn Protocol gateway webhooks so Dhaaga products automatically syndicate across national ONDC buyer apps (Paytm, Pincode, Magicpin).", bullet_style))

    story.append(PageBreak())

    # ──────────────────────────────────────────────────────────────────────────
    # 5. DIPANSHU GOLA (DATA & ANALYTICS)
    # ──────────────────────────────────────────────────────────────────────────
    story.append(Paragraph("5. Dipanshu Gola — Data & Analytics", h1_style))
    story.append(Paragraph("<b>Primary Ownership:</b> Metrics Telemetry, Artisan Payout Aggregations, Performance Monitoring, Demand Forecasting", h2_style))

    story.append(Paragraph("<b>Current Implementation Deep-Dive:</b>", body_style))
    story.append(Paragraph("• <b>Artisan Revenue Dashboard:</b> Computes monthly GMV, pending payouts, completed shipments, and active listings in real time via <code>AppViewModel.dashboardEarningsPaise</code>.", bullet_style))
    story.append(Paragraph("• <b>Client-Side Telemetry:</b> Logs operational telemetry for voice cataloging duration, AI enhancement latency, and cart conversion funnels.", bullet_style))

    story.append(Spacer(1, 4))
    story.append(Paragraph("<b>Technical Gaps, Improvements & Roadmap for Dipanshu:</b>", body_style))
    story.append(Paragraph("1. <b>Firebase BigQuery Export Pipeline:</b> Stream Firestore documents and event logs into Google BigQuery for longitudinal economic analysis on artisan income growth.", bullet_style))
    story.append(Paragraph("2. <b>Artisan Livelihood Impact Metric:</b> Formulate and display an <i>'Economic Uplift Score'</i> showing each artisan how much extra revenue they retained compared to traditional exploitative middlemen.", bullet_style))
    story.append(Paragraph("3. <b>Seasonal Demand Forecasting:</b> Build time-series models (SARIMA / Prophet) predicting demand surges for specific craft clusters ahead of festival seasons (Diwali, Durga Puja, Eid, Wedding seasons).", bullet_style))
    story.append(Paragraph("4. <b>Looker Studio Executive Dashboard:</b> Create automated reporting dashboards for craft cooperatives, IIT Madras mentorship panels, and social-impact investors.", bullet_style))

    story.append(Spacer(1, 10))

    # ──────────────────────────────────────────────────────────────────────────
    # 6. SHASHWAT UPADHYAY (RESEARCH & DOCUMENTATION)
    # ──────────────────────────────────────────────────────────────────────────
    story.append(Paragraph("6. Shashwat Upadhyay — Research & Documentation", h1_style))
    story.append(Paragraph("<b>Primary Ownership:</b> Craft Clustering, GI Tag Verification, Usability Ethnography, Govt Scheme Alignment, Vernacular Dictionaries", h2_style))

    story.append(Paragraph("<b>Current Implementation Deep-Dive:</b>", body_style))
    story.append(Paragraph("• <b>22 Official Eighth Schedule Indian Languages:</b> Curated and verified localized vocabulary dictionaries in <code>AppLanguageManager.kt</code> (Hindi, Bengali, Tamil, Telugu, Marathi, Gujarati, Kannada, Malayalam, Punjabi, Odia, Urdu, Assamese, Sanskrit, Dogri, Konkani, Maithili, Nepali, Santhali, Manipuri, Kashmiri, Bhojpuri).", bullet_style))
    story.append(Paragraph("• <b>Authentic Craft & Geography Modeling:</b> Mapped state-wise craft categories across all 28 states & 8 UTs in <code>MockData.indianStates</code>.", bullet_style))

    story.append(Spacer(1, 4))
    story.append(Paragraph("<b>Technical Gaps, Improvements & Roadmap for Shashwat:</b>", body_style))
    story.append(Paragraph("1. <b>National GI Tag Authenticity Registry:</b> Build a curated database linking crafts to their official Government of India Geographical Indications (GI) certificate numbers, protecting artisans against mass-produced fakes.", bullet_style))
    story.append(Paragraph("2. <b>Government Welfare Scheme Integration:</b> Document and map workflows integrating Dhaaga with <i>PM Vishwakarma Yojana</i>, <i>ODOP (One District One Product)</i>, and NABARD artisan credit linkages.", bullet_style))
    story.append(Paragraph("3. <b>Rural Usability Field Studies:</b> Conduct usability testing sessions with artisan communities in Rajasthan, Varanasi, and Bastar to refine vernacular terminology and non-text visual affordances.", bullet_style))
    story.append(Paragraph("4. <b>OpenAPI & System Architecture Whitepaper:</b> Maintain comprehensive API documentation, sequence diagrams, and onboarding guides for third-party developers and NGO field workers.", bullet_style))

    story.append(PageBreak())

    # ══════════════════════════════════════════════════════════════════════════
    # DATABASE & FIREBASE CLOUD ARCHITECTURE
    # ══════════════════════════════════════════════════════════════════════════
    story.append(Paragraph("Firebase Cloud Architecture & Database Schema", h1_style))
    story.append(Paragraph(
        "The Dhaaga backend is engineered on Cloud Firestore's serverless NoSQL document model, optimized for real-time offline-first sync. Below is the detailed schema structure:",
        body_style
    ))

    schema_data = [
        [
            Paragraph("Collection", table_header),
            Paragraph("Document ID", table_header),
            Paragraph("Fields & Data Types", table_header),
            Paragraph("Sync & Indexing Strategy", table_header)
        ],
        [
            Paragraph("<code>users</code>", code_style),
            Paragraph("<code>{uid}</code> (Auth UID)", code_style),
            Paragraph("• <code>name</code>: String<br/>• <code>phone</code>: String<br/>• <code>email</code>: String<br/>• <code>role</code>: 'artisan' | 'buyer'<br/>• <code>village, state</code>: String<br/>• <code>craftSpecialty</code>: String<br/>• <code>profilePhotoUrl</code>: String<br/>• <code>isKycVerified</code>: Boolean", table_cell),
            Paragraph("Cached locally in SharedPreferences. Single-document listener on profile changes.", table_cell)
        ],
        [
            Paragraph("<code>products</code>", code_style),
            Paragraph("<code>{productId}</code> (UUID)", code_style),
            Paragraph("• <code>titleEn, titleHi</code>: String<br/>• <code>descriptionEn, descriptionHi</code>: String<br/>• <code>priceListed, priceOriginal</code>: Long (paise)<br/>• <code>category, region, state</code>: String<br/>• <code>imageUrl, thumbnailUrls</code>: List&lt;String&gt;<br/>• <code>stockQuantity</code>: Int<br/>• <code>sellerId, sellerName</code>: String<br/>• <code>isGiTagged, giTagNumber</code>: String<br/>• <code>hoursToCreate</code>: Int", table_cell),
            Paragraph("Collection snapshot listener for marketplace. Composite index: <code>category ASC, createdAt DESC</code> and <code>sellerId ASC, createdAt DESC</code>.", table_cell)
        ],
        [
            Paragraph("<code>orders</code>", code_style),
            Paragraph("<code>{orderId}</code> (DHG-XXXX)", code_style),
            Paragraph("• <code>productId, productTitle</code>: String<br/>• <code>buyerId, buyerName</code>: String<br/>• <code>sellerId, sellerName</code>: String<br/>• <code>quantity</code>: Int<br/>• <code>totalAmount, sellerPayout</code>: Long<br/>• <code>paymentMethod</code>: String<br/>• <code>paymentStatus</code>: 'paid' | 'pending'<br/>• <code>isMockPayment</code>: Boolean<br/>• <code>status</code>: 'confirmed' | 'packed' | 'shipped' | 'delivered'<br/>• <code>shippingCarrier, trackingId</code>: String", table_cell),
            Paragraph("Bi-directional listeners: Artisans observe incoming orders; Buyers observe order progress. Atomic stock decrement on order creation.", table_cell)
        ],
        [
            Paragraph("<code>gi_registry</code>", code_style),
            Paragraph("<code>GI-IND-XXXX</code>", code_style),
            Paragraph("• <code>giNumber</code>: Int (e.g. 132 for Banarasi Brocade)<br/>• <code>craftName, authorizedState</code>: String<br/>• <code>clusterDistricts</code>: Array&lt;String&gt;<br/>• <code>geofencePolygon</code>: GeoPoint Array<br/>• <code>authorizedUserFormat</code>: Regex String<br/>• <code>inspectionBody, officialEmblemUrl</code>: String", table_cell),
            Paragraph("Cached master reference collection. Cross-referenced during artisan KYC and product listing to verify statutory GI protection.", table_cell)
        ]
    ]

    t_schema = Table(schema_data, colWidths=[65, 80, 240, 145])
    t_schema.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), c_primary),
        ('BOX', (0,0), (-1,-1), 1, c_border),
        ('INNERGRID', (0,0), (-1,-1), 0.5, c_border),
        ('TOPPADDING', (0,0), (-1,-1), 5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 5),
        ('LEFTPADDING', (0,0), (-1,-1), 5),
        ('RIGHTPADDING', (0,0), (-1,-1), 5),
    ]))
    story.append(t_schema)

    story.append(PageBreak())

    # ══════════════════════════════════════════════════════════════════════════
    # DEEP TECHNICAL DEFENSE & HARD ENGINEERING QUESTIONS
    # ══════════════════════════════════════════════════════════════════════════
    story.append(Paragraph("Deep Technical Defense & Architectural FAQ", h1_style))
    story.append(Paragraph(
        "Critical analysis of foundational architectural decisions, technical trade-offs, and defense of Dhaaga's engineering stack:",
        body_style
    ))

    story.append(Paragraph("<b>Q1: Why are we NOT training or self-hosting our own Image Generation / Diffusion model?</b>", h2_style))
    story.append(Paragraph("• <b>The 'Hallucination vs. Authenticity' Trap (Physical Crafts):</b> Generative diffusion models (e.g., Stable Diffusion, Midjourney, Flux) synthesize images from random latent noise based on text prompts. If an artisan photographs an authentic handwoven Sambalpuri Ikat, a generative model will hallucinate a <i>fictional pattern</i> that does not match the physical craft. Selling synthetic fabric patterns leads to immediate consumer fraud and buyer returns. Dhaaga requires <b>Subject-Preserving Enhancement (Segmentation + Inpainting)</b>, NOT generative hallucination from noise. Cloud vision pipelines (MagicHour / Gemini Vision + <code>ImageSegmentationHelper.kt</code>) preserve 100% of the raw physical craft pixels while re-rendering only the studio background and lighting.", bullet_style))
    story.append(Paragraph("• <b>Infrastructure & GPU Economics:</b> Dedicated GPU instances (NVIDIA A100/H100 or L40S) cost $1,800 to $3,200 per month per node. Rural artisan uploads are heavily bursty (peaks during daylight 10 AM – 4 PM, quiet at night). Self-hosting burns expensive idle GPU hours. Serverless multimodal API invocations charge purely per-call (~$0.015 per item) with <b>zero idle operational cost</b>.", bullet_style))
    story.append(Paragraph("• <b>Edge Constraints on Sub-₹12,000 Rural Devices:</b> Over 92% of artisans use budget Android phones (MediaTek Helio / Unisoc / Snapdragon 680) with 3GB–4GB RAM. On-device diffusion (even 4-bit SD-Turbo) requires >6GB free RAM and a high-throughput NPU. Running it locally triggers immediate OutOfMemory (OOM) kernel kills, thermal throttling, and severe battery drain.", bullet_style))
    story.append(Paragraph("• <b>Indian Heritage Craft Dataset Scarcity:</b> Training a proprietary foundation model requires 500,000+ curated high-resolution pairs of authentic Indian handicrafts across all 28 states, which currently does not exist in open research. Dhaaga's Phase 2 strategy is to use our live marketplace to organically build India's first verified handicraft image repository before training a custom LoRA adapter.", bullet_style))

    story.append(Spacer(1, 6))
    story.append(Paragraph("<b>Q2: How do we prevent urban traders & factory counterfeiters from creating fake artisan accounts?</b>", h2_style))
    story.append(Paragraph("• <b>Geofenced Cluster Verification:</b> Device GPS during registration must match official rural artisan cluster boundaries (e.g., Channapatna for toys, Madhubani for Mithila art). Accounts attempting registration from urban industrial zones are flagged for manual audit.", bullet_style))
    story.append(Paragraph("• <b>Live Video Proof-of-Work (PoW):</b> Artisans record a 5-second video clip of their active loom, pottery wheel, or carving bench. Computer vision models verify active hand-tools and work-in-progress.", bullet_style))
    story.append(Paragraph("• <b>Cooperative / Guild Cryptographic Vouching:</b> Master accounts held by registered weaver societies and SHGs cryptographically endorse authentic member artisans.", bullet_style))

    story.append(Spacer(1, 6))
    story.append(Paragraph("<b>Q3: How does the Voice Auto-Cataloger handle heavy rural accents and ambient workshop noise?</b>", h2_style))
    story.append(Paragraph("• <b>Acoustic Pre-Filtering:</b> Uses Android's native <code>NoiseSuppressor</code> hardware effect API on the audio input stream to filter repetitive handloom clatter and hammer strikes.", bullet_style))
    story.append(Paragraph("• <b>Colloquial Craft Glossaries:</b> Speech engines often fail on rural terms (e.g., <i>zari</i>, <i>kadwa</i>, <i>ikat</i>, <i>dhokra</i>, <i>kalamkari</i>). Dhaaga feeds raw phonetic text to Gemini 2.5 Flash with prompt-injected regional craft dictionaries, standardizing dialect terms into professional catalog fields.", bullet_style))

    story.append(Spacer(1, 6))
    story.append(Paragraph("<b>Q4: How does the app prevent race-condition overselling of one-of-a-kind handmade items?</b>", h2_style))
    story.append(Paragraph("• <b>Firestore Atomic Transactions:</b> Checkout executes a Firestore <code>runTransaction</code> that locks the product document, reads current <code>stockQuantity</code>, and commits decrement only if <code>stockQuantity >= requestedQuantity</code>. If concurrent checkouts race, the second transaction is safely rolled back with a 'Craft Sold Out' notification.", bullet_style))

    story.append(PageBreak())

    # ══════════════════════════════════════════════════════════════════════════
    # GEOGRAPHICAL INDICATION (GI) TAG IMPLEMENTATION ARCHITECTURE
    # ══════════════════════════════════════════════════════════════════════════
    story.append(Paragraph("Geographical Indication (GI) Tag Implementation Architecture", h1_style))
    story.append(Paragraph(
        "A <b>Geographical Indication (GI)</b> is an intellectual property certification governed by the Government of India's <i>Geographical Indications of Goods (Registration and Protection) Act, 1999</i>. It certifies that a craft possesses qualities, reputation, or characteristics attributable to its specific territory (e.g., GI #132 Banarasi Brocade, GI #194 Kashmir Pashmina, GI #3 Channapatna Toys, GI #100 Madhubani Art).",
        body_style
    ))

    gi_steps_data = [
        [
            Paragraph("Implementation Layer", table_header),
            Paragraph("Technical Architecture & Workflow", table_header),
            Paragraph("Codebase Mapping", table_header)
        ],
        [
            Paragraph("<b>1. Master GI Registry</b>", table_cell),
            Paragraph("Dedicated Firestore collection (<code>gi_registry</code>) cataloging statutory GI numbers, official names, GeoJSON district boundary polygons, and inspection body details (CGPDTM).", table_cell),
            Paragraph("<code>firestore.rules<br/>gi_registry collection</code>", code_style)
        ],
        [
            Paragraph("<b>2. Artisan Authorized User KYC</b>", table_cell),
            Paragraph("During seller onboarding in <code>ProfileSetupScreen.kt</code>, the artisan inputs their government-issued <b>Authorized User Number</b> (format: <code>AU/XXXX/GI/XXX</code>) or uploads their physical GI Artisan Identity Card for OCR verification.", table_cell),
            Paragraph("<code>ProfileSetupScreen.kt<br/>UserModel.kt (isGiVerified)</code>", code_style)
        ],
        [
            Paragraph("<b>3. Geofenced Cluster Check</b>", table_cell),
            Paragraph("When an artisan lists a craft, GPS coordinates are validated against the statutory polygon of that craft (e.g. Banarasi Silk must originate from Varanasi/Chandauli/Mirzapur/Jaunpur districts).", table_cell),
            Paragraph("<code>AddProductScreen.kt<br/>LocationManagerHelper</code>", code_style)
        ],
        [
            Paragraph("<b>4. Buyer Trust Badging & Provenance Sheet</b>", table_cell),
            Paragraph("Verified products display a gold-embossed Government of India GI Emblem on product cards. Tapping opens an interactive Provenance Sheet showing the weaver's authorized registration, craft heritage history, and certificate link.", table_cell),
            Paragraph("<code>ProductModel.kt<br/>ProductDetailScreen.kt</code>", code_style)
        ],
        [
            Paragraph("<b>5. Cryptographic QR Label on Packaging</b>", table_cell),
            Paragraph("When an order is confirmed, Dhaaga's backend generates a cryptographically signed QR code printed directly on the Delhivery courier shipping label (e.g., <code>https://dhaaga.in/verify/GI-132-AU8921</code>). Scanning displays the artisan's photograph, workshop GPS, and GI certificate.", table_cell),
            Paragraph("<code>OrderModel.kt<br/>MockPaymentDialog.kt</code>", code_style)
        ]
    ]

    t_gi = Table(gi_steps_data, colWidths=[110, 290, 130])
    t_gi.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#B8860B")),
        ('BOX', (0,0), (-1,-1), 1, c_border),
        ('INNERGRID', (0,0), (-1,-1), 0.5, c_border),
        ('TOPPADDING', (0,0), (-1,-1), 5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 5),
        ('LEFTPADDING', (0,0), (-1,-1), 6),
        ('RIGHTPADDING', (0,0), (-1,-1), 6),
    ]))
    story.append(t_gi)

    story.append(Spacer(1, 14))
    story.append(Paragraph("Technical Gap Analysis & Strategic Roadmap", h1_style))
    story.append(Paragraph(
        "While Dhaaga has achieved a rock-solid, production-ready MVP with 0 build errors, scaling to 100,000+ artisans across India requires completing the following strategic milestones:",
        body_style
    ))

    roadmap_data = [
        [
            Paragraph("Milestone Phase", table_header),
            Paragraph("Objective & Architecture Target", table_header),
            Paragraph("Lead Assignee", table_header),
            Paragraph("Target Quarter", table_header)
        ],
        [
            Paragraph("<b>Phase 1: Real Money & Escrow</b>", table_cell),
            Paragraph("Migrate Mock Payment to Razorpay Route UPI split payments. Escrow hold with instant release upon Delhivery AWB delivery webhook.", table_cell),
            Paragraph("Garvit (Full-Stack)<br/>Ritesh (Backend)", table_cell),
            Paragraph("Q4 2026", table_cell)
        ],
        [
            Paragraph("<b>Phase 2: Offline & Edge AI</b>", table_cell),
            Paragraph("Implement Room Database SQLite with background WorkManager sync. Package Gemini Nano on-device models for 100% offline catalog creation.", table_cell),
            Paragraph("Garvit (Full-Stack)<br/>Siddhi (AI/ML)", table_cell),
            Paragraph("Q1 2027", table_cell)
        ],
        [
            Paragraph("<b>Phase 3: National ONDC Launch</b>", table_cell),
            Paragraph("Deploy Beckn Protocol adapter to syndicate Dhaaga artisan inventory across India's ONDC network. Implement PM Vishwakarma verification.", table_cell),
            Paragraph("Ritesh (Backend)<br/>Shashwat (Research)", table_cell),
            Paragraph("Q2 2027", table_cell)
        ],
        [
            Paragraph("<b>Phase 4: Impact & Predictive Scaling</b>", table_cell),
            Paragraph("BigQuery predictive inventory models for festive craft surges. Launch Looker Studio institutional dashboard and vernacular audio reviews.", table_cell),
            Paragraph("Dipanshu (Analytics)<br/>Sargam (Design Lead)", table_cell),
            Paragraph("Q3 2027", table_cell)
        ]
    ]

    t_road = Table(roadmap_data, colWidths=[105, 235, 110, 80])
    t_road.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), c_accent),
        ('BOX', (0,0), (-1,-1), 1, c_border),
        ('INNERGRID', (0,0), (-1,-1), 0.5, c_border),
        ('TOPPADDING', (0,0), (-1,-1), 5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 5),
        ('LEFTPADDING', (0,0), (-1,-1), 5),
        ('RIGHTPADDING', (0,0), (-1,-1), 5),
    ]))
    story.append(t_road)

    story.append(Spacer(1, 20))
    story.append(HRFlowable(width="100%", thickness=1, color=c_primary, spaceBefore=5, spaceAfter=10))
    story.append(Paragraph(
        "<b>Summary & Next Steps:</b> This document establishes the master technical blueprint for Dhaaga. Garvit Sharma holds full administrative control over the architecture, AppViewModel reactive pipelines, and Firestore orchestration. Each team member should execute against their assigned domain roadmap to prepare Dhaaga for large-scale pilot deployment in artisan clusters across India.",
        body_style
    ))

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"PDF built successfully: {filename}")

if __name__ == "__main__":
    build_pdf("f:/Source Codes/Dhaaga/Dhaaga_Master_Technical_Report.pdf")
