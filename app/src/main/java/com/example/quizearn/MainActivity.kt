package com.example.quizearn

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class MainActivity : Activity() {

    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout
    private lateinit var title: TextView
    private lateinit var trophyText: TextView

    private val prefs by lazy {
        getSharedPreferences("QuizEarnData", MODE_PRIVATE)
    }

    private var trophies = 0
    private var userName = ""
    private var userEmail = ""
    private var currentQuestion = 0
    private var answered = false
    private var rewardedAd: RewardedAd? = null
    private val rewardedAdUnitId = "ca-app-pub-3940256099942544/5224354917"

    private val questions = arrayOf(
        "What is the capital of India?",
        "Which planet is known as the Red Planet?",
        "How many days are there in a week?",
        "What is 5 × 5?",
        "Which is the largest ocean?",
        "Who wrote the national anthem of India?",
        "How many colors are there in a rainbow?",
        "Which animal is known as the King of the Jungle?",
        "What is the currency of India?",
        "Which gas do humans need to breathe?"
    )

    private val options = arrayOf(
        arrayOf("Mumbai", "New Delhi", "Kolkata", "Chennai"),
        arrayOf("Earth", "Mars", "Jupiter", "Venus"),
        arrayOf("5", "6", "7", "8"),
        arrayOf("20", "25", "30", "35"),
        arrayOf("Atlantic", "Indian", "Pacific", "Arctic"),
        arrayOf("Rabindranath Tagore", "Mahatma Gandhi", "A.P.J. Abdul Kalam", "Jawaharlal Nehru"),
        arrayOf("5", "6", "7", "8"),
        arrayOf("Tiger", "Lion", "Elephant", "Horse"),
        arrayOf("Dollar", "Rupee", "Yen", "Euro"),
        arrayOf("Nitrogen", "Oxygen", "Carbon Dioxide", "Hydrogen")
    )

    private val correctAnswers = intArrayOf(
        1, 1, 2, 1, 2, 0, 2, 1, 1, 1
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {
            loadRewardedAd()
        }

        trophies = prefs.getInt("trophies", 0)
        userName = prefs.getString("name", "") ?: ""
        userEmail = prefs.getString("email", "") ?: ""

        if (userName.isEmpty() || userEmail.isEmpty()) showLogin() else showHome()
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            this,
            rewardedAdUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun text(
        value: String,
        size: Float,
        bold: Boolean = false
    ): TextView {
        val t = TextView(this)
        t.text = value
        t.textSize = size
        t.setTextColor(Color.WHITE)

        if (bold) {
            t.setTypeface(null, Typeface.BOLD)
        }

        t.setPadding(dp(4), dp(4), dp(4), dp(4))
        return t
    }

    private fun cardBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.rgb(28, 31, 48))
            cornerRadius = dp(20).toFloat()
        }
    }

    private fun buttonBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.rgb(74, 88, 180))
            cornerRadius = dp(14).toFloat()
        }
    }

    private fun makeButton(label: String): Button {
        val b = Button(this)
        b.text = label
        b.textSize = 15f
        b.setTextColor(Color.WHITE)
        b.setAllCaps(false)
        b.background = buttonBackground()

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(54)
        )

        params.setMargins(0, dp(7), 0, dp(7))
        b.layoutParams = params

        return b
    }

    private fun baseLayout(screenTitle: String): LinearLayout {

        root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.rgb(13, 15, 27))

        val header = LinearLayout(this)
        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = Gravity.CENTER_VERTICAL
        header.setPadding(dp(18), dp(15), dp(18), dp(10))

        title = text(screenTitle, 23f, true)

        val titleParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        header.addView(title, titleParams)

        trophyText = text("🏆 $trophies", 17f, true)
        header.addView(trophyText)

        root.addView(header)

        val scroll = ScrollView(this)
        content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(18), dp(5), dp(18), dp(25))

        scroll.addView(content)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)

        return content
    }

    private fun saveData() {
        prefs.edit()
            .putInt("trophies", trophies)
            .apply()
    }

    private fun showLogin() {

        val c = baseLayout("QuizEarn")

        val logo = text("🧠  QUIZ EARN", 30f, true)
        logo.gravity = Gravity.CENTER
        logo.setPadding(0, dp(35), 0, dp(10))
        c.addView(logo)

        val subtitle = text(
            "Learn • Play • Earn Trophies",
            16f
        )
        subtitle.gravity = Gravity.CENTER
        c.addView(subtitle)

        val nameInput = EditText(this)
        nameInput.hint = "Enter your name"
        nameInput.setTextColor(Color.WHITE)
        nameInput.setHintTextColor(Color.GRAY)

        val emailInput = EditText(this)
        emailInput.hint = "Email address"
        emailInput.inputType = 33
        emailInput.setTextColor(Color.WHITE)
        emailInput.setHintTextColor(Color.GRAY)

        val passwordInput = EditText(this)
        passwordInput.hint = "Password"
        passwordInput.inputType = 129
        passwordInput.setTextColor(Color.WHITE)
        passwordInput.setHintTextColor(Color.GRAY)

        c.addView(nameInput)
        c.addView(emailInput)
        c.addView(passwordInput)

        val login = makeButton("🚀  Login & Continue")

        login.setOnClickListener {

            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            userName = name
            userEmail = email

            prefs.edit()
                .putString("name", userName)
                .putString("email", userEmail)
                .apply()

            showHome()
        }

        c.addView(login)

        val note = text(
            "Your progress is saved on this device.",
            13f
        )
        note.gravity = Gravity.CENTER
        note.setTextColor(Color.LTGRAY)

        c.addView(note)
    }
    private fun showHome() {

        val c = baseLayout("QuizEarn")

        val welcome = text(
            "Hello, ${if (userName.isEmpty()) "Player" else userName} 👋",
            24f,
            true
        )
        c.addView(welcome)

        val sub = text(
            "Ready for today's challenge?",
            14f
        )
        sub.setTextColor(Color.LTGRAY)
        c.addView(sub)

        val trophyCard = LinearLayout(this)
        trophyCard.orientation = LinearLayout.VERTICAL
        trophyCard.setPadding(dp(20), dp(20), dp(20), dp(20))
        trophyCard.background = cardBackground()

        val cardTitle = text("🏆  Your Trophies", 16f, true)
        trophyCard.addView(cardTitle)

        val count = text("$trophies", 38f, true)
        trophyCard.addView(count)

        val small = text(
            "Collect trophies by answering correctly",
            13f
        )
        small.setTextColor(Color.LTGRAY)
        trophyCard.addView(small)

        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        cardParams.setMargins(0, dp(18), 0, dp(12))
        c.addView(trophyCard, cardParams)

        val challenge = LinearLayout(this)
        challenge.orientation = LinearLayout.VERTICAL
        challenge.setPadding(dp(20), dp(18), dp(20), dp(18))
        challenge.background = cardBackground()

        val challengeTitle = text(
            "🔥  DAILY CHALLENGE",
            14f,
            true
        )
        challenge.addView(challengeTitle)

        val challengeText = text(
            "Answer 10 questions and earn up to 10 trophies.",
            18f,
            true
        )
        challenge.addView(challengeText)

        val start = makeButton("▶  START QUIZ")

        start.setOnClickListener {
            currentQuestion = 0
            showQuiz()
        }

        challenge.addView(start)
        c.addView(challenge)

        val catTitle = text(
            "Explore Categories",
            19f,
            true
        )
        catTitle.setPadding(0, dp(22), 0, dp(8))
        c.addView(catTitle)

        val gk = makeButton("🌍  General Knowledge")
        val science = makeButton("🔬  Science")
        val world = makeButton("🌎  World")

        gk.setOnClickListener {
            Toast.makeText(
                this,
                "General Knowledge selected",
                Toast.LENGTH_SHORT
            ).show()

            currentQuestion = 0
            showQuiz()
        }

        science.setOnClickListener {
            Toast.makeText(
                this,
                "Science category coming soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        world.setOnClickListener {
            Toast.makeText(
                this,
                "World category coming soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        c.addView(gk)
        c.addView(science)
        c.addView(world)

        val rewards = makeButton("🎁  Rewards & Wallet")

        rewards.setOnClickListener {
            showWallet()
        }

        c.addView(rewards)

        val profile = makeButton("👤  My Profile")

        profile.setOnClickListener {
            showProfile()
        }

        c.addView(profile)

        val logout = makeButton("↪  Logout")

        logout.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you want to logout?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Logout") { _, _ ->
                    prefs.edit().clear().apply()
                    userName = ""
                    userEmail = ""
                    trophies = 0
                    showLogin()
                }
                .show()
        }

        c.addView(logout)

        val streak = text(
            "🔥 Daily Streak: 1 day",
            15f,
            true
        )
        streak.gravity = Gravity.CENTER
        streak.setPadding(0, dp(20), 0, dp(5))
        c.addView(streak)
    }

    private fun showQuiz() {

        val c = baseLayout(
            "Question ${currentQuestion + 1}/10"
        )

        if (currentQuestion >= questions.size) {
            showQuizComplete()
            return
        }

        answered = false

        val progress = ProgressBar(
            this,
            null,
            android.R.attr.progressBarStyleHorizontal
        )

        progress.max = questions.size
        progress.progress = currentQuestion + 1

        c.addView(
            progress,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(8)
            )
        )

        val questionCard = LinearLayout(this)
        questionCard.orientation = LinearLayout.VERTICAL
        questionCard.setPadding(
            dp(20),
            dp(25),
            dp(20),
            dp(25)
        )
        questionCard.background = cardBackground()

        val q = text(
            questions[currentQuestion],
            22f,
            true
        )

        questionCard.addView(q)

        val qHint = text(
            "Choose the correct answer",
            13f
        )

        qHint.setTextColor(Color.LTGRAY)
        questionCard.addView(qHint)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            0,
            dp(20),
            0,
            dp(12)
        )

        c.addView(questionCard, params)

        for (i in 0 until 4) {

            val answer = makeButton(
                "${('A'.code + i).toChar()}.  ${options[currentQuestion][i]}"
            )

            val index = i

            answer.setOnClickListener {

                if (answered) return@setOnClickListener

                answered = true

                if (index == correctAnswers[currentQuestion]) {

                    answer.setTextColor(Color.GREEN)

                    Toast.makeText(
                        this,
                        "Correct answer! 🎉",
                        Toast.LENGTH_SHORT
                    ).show()

                    showRewardAd()

                } else {

                    answer.setTextColor(Color.RED)

                    Toast.makeText(
                        this,
                        "Wrong answer ❌",
                        Toast.LENGTH_SHORT
                    ).show()

                    AlertDialog.Builder(this)
                        .setTitle("Wrong Answer")
                        .setMessage(
                            "The correct answer was: " +
                                options[currentQuestion]
                                    [correctAnswers[currentQuestion]]
                        )
                        .setPositiveButton("Next") { _, _ ->
                            currentQuestion++
                            showQuiz()
                        }
                        .show()
                }
            }

            c.addView(answer)
        }

        val back = makeButton("←  Back to Home")

        back.setOnClickListener {
            showHome()
        }

        c.addView(back)
    }
    private fun showRewardAd() {

        val ad = rewardedAd

        if (ad == null) {
            AlertDialog.Builder(this)
                .setTitle("📺 Ad Not Ready")
                .setMessage(
                    "Advertisement is still loading.\\n\\n" +
                    "Please try again in a few seconds."
                )
                .setPositiveButton("OK", null)
                .show()

            loadRewardedAd()
            return
        }

        rewardedAd = null

        ad.fullScreenContentCallback =
            object : com.google.android.gms.ads.FullScreenContentCallback() {

                override fun onAdDismissedFullScreenContent() {
                    loadRewardedAd()
                }

                override fun onAdFailedToShowFullScreenContent(
                    adError: com.google.android.gms.ads.AdError
                ) {
                    loadRewardedAd()
                }
            }

        ad.show(this) {
            trophies++
            saveData()

            Toast.makeText(
                this@MainActivity,
                "🏆 +1 Trophy earned!",
                Toast.LENGTH_LONG
            ).show()

            currentQuestion++

            if (trophies >= 25) {
                showMilestone()
            } else {
                showQuiz()
            }
        }
    }

    private fun showMilestone() {

        AlertDialog.Builder(this)
            .setTitle("🎉 Reward Unlocked!")
            .setMessage(
                "Congratulations! 🏆\n\n" +
                "You have collected 25 trophies.\n\n" +
                "🎁 A special reward is waiting for you.\n\n" +
                "Complete the sponsored offer to unlock your reward."
            )
            .setPositiveButton("📲 COMPLETE OFFER") { _, _ ->
                showOfferScreen()
            }
            .setNegativeButton("Continue Quiz") { _, _ ->
                showQuiz()
            }
            .setCancelable(false)
            .show()
    }

    private fun showOfferScreen() {

        val c = baseLayout("🎁 Special Reward")

        val heading = text(
            "📲 Complete Offer",
            26f,
            true
        )
        heading.gravity = Gravity.CENTER
        c.addView(heading)

        val info = text(
            "\nYou reached 25 trophies! 🏆\n\n" +
            "Complete a sponsored app offer to unlock your reward.\n\n" +
            "After the offer is successfully verified, " +
            "your reward will be credited to your wallet.",
            17f,
            false
        )
        info.setTextColor(Color.LTGRAY)
        c.addView(info)

        val offerButton = makeButton("📲  START SPONSORED OFFER")

        offerButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Sponsored Offer")
                .setMessage(
                    "The sponsored offer system is not connected yet.\n\n" +
                    "We will connect a genuine offerwall here " +
                    "so completed app installs can be verified."
                )
                .setPositiveButton("OK", null)
                .show()
        }

        c.addView(offerButton)

        val back = makeButton("🏠  Back to Home")

        back.setOnClickListener {
            showHome()
        }

        c.addView(back)
    }

    private fun showQuizComplete() {

        val c = baseLayout("Quiz Complete 🎉")

        val trophy = text(
            "🏆",
            60f,
            true
        )
        trophy.gravity = Gravity.CENTER
        c.addView(trophy)

        val complete = text(
            "Excellent Work!",
            28f,
            true
        )
        complete.gravity = Gravity.CENTER
        c.addView(complete)

        val score = text(
            "You completed today's quiz.",
            17f
        )
        score.gravity = Gravity.CENTER
        score.setTextColor(Color.LTGRAY)
        c.addView(score)

        val total = text(
            "Total Trophies: $trophies",
            22f,
            true
        )
        total.gravity = Gravity.CENTER
        total.setPadding(0, dp(25), 0, dp(15))
        c.addView(total)

        val home = makeButton("🏠  Back to Home")

        home.setOnClickListener {
            showHome()
        }

        c.addView(home)

        val rewards = makeButton("🎁  View Rewards")

        rewards.setOnClickListener {
            showWallet()
        }

        c.addView(rewards)
    }

    private fun showWallet() {

        val c = baseLayout("Rewards & Wallet")

        val titleText = text(
            "🎁 Your Rewards",
            25f,
            true
        )
        c.addView(titleText)

        val trophyCard = LinearLayout(this)
        trophyCard.orientation = LinearLayout.VERTICAL
        trophyCard.setPadding(
            dp(20),
            dp(20),
            dp(20),
            dp(20)
        )
        trophyCard.background = cardBackground()

        val trophyLabel = text(
            "🏆 Trophy Balance",
            16f,
            true
        )
        trophyCard.addView(trophyLabel)

        val balance = text(
            "$trophies Trophies",
            30f,
            true
        )
        trophyCard.addView(balance)

        c.addView(
            trophyCard,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(18), 0, dp(18))
            }
        )

        val rewardInfo = text(
            "🎯 Reward Milestone\n\n" +
            "Collect 25 trophies to unlock the next reward stage.\n\n" +
            "Current progress: $trophies / 25",
            17f,
            false
        )

        rewardInfo.setPadding(
            dp(5),
            dp(10),
            dp(5),
            dp(20)
        )

        c.addView(rewardInfo)

        val progress = ProgressBar(
            this,
            null,
            android.R.attr.progressBarStyleHorizontal
        )

        progress.max = 25
        progress.progress = trophies.coerceAtMost(25)

        c.addView(
            progress,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(10)
            )
        )

        val cashInfo = text(
            "\n💰 Wallet\n\n" +
            "Your future reward balance will appear here.\n" +
            "Real-money rewards require a secure backend and verified payment system.",
            15f
        )

        cashInfo.setTextColor(Color.LTGRAY)
        c.addView(cashInfo)

        val home = makeButton("🏠  Back to Home")

        home.setOnClickListener {
            showHome()
        }

        c.addView(home)
    }

    private fun showProfile() {

        val c = baseLayout("My Profile")

        val avatar = text(
            "👤",
            55f,
            true
        )
        avatar.gravity = Gravity.CENTER
        c.addView(avatar)

        val name = text(
            userName.ifEmpty { "Player" },
            25f,
            true
        )
        name.gravity = Gravity.CENTER
        c.addView(name)

        val email = text(
            userEmail.ifEmpty { "No email" },
            15f
        )
        email.gravity = Gravity.CENTER
        email.setTextColor(Color.LTGRAY)
        c.addView(email)

        val stats = LinearLayout(this)
        stats.orientation = LinearLayout.VERTICAL
        stats.setPadding(
            dp(20),
            dp(20),
            dp(20),
            dp(20)
        )
        stats.background = cardBackground()

        stats.addView(
            text("🏆 Trophies: $trophies", 18f, true)
        )

        stats.addView(
            text("🔥 Daily Streak: 1 day", 18f, true)
        )

        stats.addView(
            text("⭐ Level: ${(trophies / 10) + 1}", 18f, true)
        )

        c.addView(
            stats,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp(25), 0, dp(20))
            }
        )

        val home = makeButton("🏠  Home")

        home.setOnClickListener {
            showHome()
        }

        c.addView(home)
    }

    override fun onBackPressed() {

        showHome()
    }
}
