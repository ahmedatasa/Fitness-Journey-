package com.fitnessjourney.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Navy = Color(0xFF10233F)
private val Teal = Color(0xFF20B8A6)
private val Pale = Color(0xFFF3F7FA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("fitness_journey_local", Context.MODE_PRIVATE)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Navy, secondary = Teal, background = Pale)) {
                App(prefs)
            }
        }
    }
}

@Composable
private fun App(p: android.content.SharedPreferences) {
    var name by remember { mutableStateOf(p.getString("name", "") ?: "") }
    var age by remember { mutableStateOf(p.getString("age", "") ?: "") }
    var height by remember { mutableStateOf(p.getString("height", "") ?: "") }
    var weight by remember { mutableStateOf(p.getString("weight", "") ?: "") }
    var goal by remember { mutableStateOf(p.getString("goal", "") ?: "") }
    var activity by remember { mutableStateOf(p.getString("activity", "قليل الحركة") ?: "قليل الحركة") }
    var place by remember { mutableStateOf(p.getString("place", "البيت") ?: "البيت") }
    var level by remember { mutableStateOf(p.getString("level", "مبتدئ") ?: "مبتدئ") }
    var food by remember { mutableStateOf(p.getString("food", "أكل مصري منزلي") ?: "أكل مصري منزلي") }
    var page by remember { mutableStateOf(if (p.getBoolean("ready", false)) "الرئيسية" else "إعداد الحساب") }
    var steps by remember { mutableIntStateOf(p.getInt("steps", 0)) }
    var walk by remember { mutableIntStateOf(p.getInt("walk", 0)) }
    var run by remember { mutableIntStateOf(p.getInt("run", 0)) }
    var logs by remember { mutableStateOf((p.getString("logs", "") ?: "").split("\n").filter { it.isNotBlank() }) }
    var dialog by remember { mutableStateOf(false) }
    var entryType by remember { mutableStateOf("وجبة") }
    var entryName by remember { mutableStateOf("") }
    var entryNote by remember { mutableStateOf("") }
    var newWeight by remember { mutableStateOf(weight) }

    fun saveProfile() {
        val a = age.toIntOrNull()
        val h = height.toFloatOrNull()
        val w = weight.toFloatOrNull()
        val g = goal.toFloatOrNull()
        if (a != null && a in 18..100 && h != null && h in 100f..250f && w != null && w in 30f..350f && g != null && g in 30f..350f) {
            p.edit().putString("name", name).putString("age", age).putString("height", height)
                .putString("weight", weight).putString("goal", goal).putString("activity", activity)
                .putString("place", place).putString("level", level).putString("food", food).putBoolean("ready", true).apply()
            page = "الرئيسية"
        }
    }
    fun addLog(type: String, title: String, note: String = "") {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        logs = listOf(date + "|" + type + "|" + title + "|" + note) + logs
        p.edit().putString("logs", logs.joinToString("\n")).apply()
    }

    if (page == "إعداد الحساب" || page == "الملف الشخصي") {
        LazyColumn(Modifier.fillMaxSize().background(Pale).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 28.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Navy), shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.fillMaxWidth().padding(22.dp)) {
                        Icon(Icons.Default.Favorite, null, tint = Teal, modifier = Modifier.size(36.dp))
                        Text("رحلتك تبدأ هنا", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text("سجل بياناتك لننشئ بداية تناسبك. يمكنك تعديلها لاحقًا.", color = Color.White)
                    }
                }
            }
            item { Field("الاسم (اختياري)", name, { name = it }) }
            item { Field("العمر", age, { age = it }, true) }
            item { Field("الطول بالسنتيمتر", height, { height = it }, true) }
            item { Field("الوزن الحالي بالكجم", weight, { weight = it }, true) }
            item { Field("الوزن المستهدف بالكجم", goal, { goal = it }, true) }
            item { SelectField("النشاط اليومي", activity, listOf("قليل الحركة", "متوسط", "مرتفع")) { activity = it } }
            item { SelectField("مكان التمرين", place, listOf("البيت", "الجيم", "البيت والجيم")) { place = it } }
            item { SelectField("الخبرة الرياضية", level, listOf("مبتدئ", "متوسط", "عودة بعد انقطاع")) { level = it } }
            item { SelectField("تفضيل الطعام", food, listOf("أكل مصري منزلي", "مرن حسب الميزانية", "نباتي")) { food = it } }
            item { Button(onClick = { saveProfile() }, modifier = Modifier.fillMaxWidth().height(54.dp)) { Text("حفظ البيانات وإنشاء خطتي") } }
            item { Text("التطبيق أداة مساعدة عامة وليس بديلًا عن الطبيب أو اختصاصي التغذية.", color = Color.Gray, fontSize = 12.sp) }
        }
        return
    }

    Scaffold(
        containerColor = Pale,
        topBar = {
            Column(Modifier.fillMaxWidth().background(Navy).padding(20.dp)) {
                Text("FITNESS JOURNEY", color = Teal, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontSize = 12.sp)
                Text(page, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                listOf("الرئيسية" to Icons.Default.Home, "النشاط" to Icons.Default.DirectionsWalk, "الوجبات" to Icons.Default.Restaurant, "التقدم" to Icons.Default.ShowChart).forEach { item ->
                    NavigationBarItem(selected = page == item.first, onClick = { page = item.first }, icon = { Icon(item.second, item.first) }, label = { Text(item.first, fontSize = 10.sp) })
                }
            }
        }
    ) { pad ->
        when (page) {
            "الرئيسية" -> LazyColumn(Modifier.padding(pad).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
                item { Text("أهلًا " + name.ifBlank { "بطل" } + " 👋", fontSize = 24.sp, color = Navy, fontWeight = FontWeight.Bold); Text("خطوة صغيرة كل يوم تصنع فرقًا كبيرًا.", color = Color.Gray) }
                item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Metric("وزنك الحالي", weight + " كجم", Icons.Default.MonitorWeight, Modifier.weight(1f)); Metric("هدفك", goal + " كجم", Icons.Default.Flag, Modifier.weight(1f)) } }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Navy), shape = RoundedCornerShape(22.dp)) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("رحلتك نحو الهدف", color = Color.White)
                            Text("المتبقي: " + String.format(Locale.US, "%.1f", ((weight.toFloatOrNull() ?: 0f) - (goal.toFloatOrNull() ?: 0f)).coerceAtLeast(0f)) + " كجم", color = Teal, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            Text("مستوى النشاط: " + activity + " • التمرين: " + place, color = Color.White)
                        }
                    }
                }
                item {
                    Button(onClick = { page = "خطة الأسبوع" }, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) {
                        Icon(Icons.Default.CalendarMonth, null); Text("  اعرض خطتي الأسبوعية")
                    }
                }
                item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Metric("الخطوات", steps.toString(), Icons.Default.DirectionsWalk, Modifier.weight(1f)); Metric("المشي", walk.toString() + " د", Icons.Default.DirectionsWalk, Modifier.weight(1f)); Metric("الجري", run.toString() + " د", Icons.Default.DirectionsRun, Modifier.weight(1f)) } }
                item {
                    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("ابدأ خطتك", fontSize = 20.sp, color = Navy, fontWeight = FontWeight.Bold)
                            Text("دليل بصري للتمارين والوجبات، مع شرح الأداء والأخطاء الشائعة.", color = Color.Gray)
                            Button(onClick = { page = "دليل التمارين" }, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.FitnessCenter, null); Text("  استكشف التمارين بالشرح")
                            }
                            OutlinedButton(onClick = { page = "دليل الوجبات" }, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Restaurant, null); Text("  استكشف الوجبات المصرية")
                            }
                        }
                    }
                }
                item { Text("تسجيل سريع", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Navy) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { entryType = "وجبة"; dialog = true }, modifier = Modifier.weight(1f)) { Icon(Icons.Default.Restaurant, null); Text(" وجبة") }
                        Button(onClick = { entryType = "تمرين"; dialog = true }, modifier = Modifier.weight(1f)) { Icon(Icons.Default.FitnessCenter, null); Text(" تمرين") }
                    }
                }
                item { Text("آخر التسجيلات", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Navy) }
                if (logs.isEmpty()) item { Text("لا توجد تسجيلات بعد.", color = Color.Gray) }
                items(logs.take(5)) { LogItem(it) }
            }
            "خطة الأسبوع" -> {
                val h = (height.toFloatOrNull() ?: 0f) / 100f
                val w = weight.toFloatOrNull() ?: 0f
                val bmi = if (h > 0f) w / (h * h) else 0f
                val bmiLabel = when { bmi <= 0f -> "أدخل الطول والوزن لحساب المؤشر"; bmi < 18.5f -> "أقل من النطاق المعتاد"; bmi < 25f -> "ضمن النطاق المعتاد"; bmi < 30f -> "أعلى من النطاق المعتاد"; else -> "أعلى من النطاق المعتاد" }
                val gentle = level == "مبتدئ" || activity == "قليل الحركة"
                val duration = if (gentle) "10–15 دقيقة" else "20–30 دقيقة"
                val strengthA = if (place == "الجيم") "تمارين مقاومة على أجهزة مناسبة للمبتدئين بإشراف عند الحاجة، مع أوزان خفيفة وأداء مضبوط." else "جلوس ووقوف من كرسي، ضغط حائط، ورفع الكعبين؛ مجموعة أو مجموعتان حسب قدرتك."
                val strengthB = if (place == "الجيم") "كرر تمارين المقاومة بأوزان مريحة، ولا ترفع الحمل إلا بعد إتقان الحركة." else "كرر تمارين القوة المنزلية مع التركيز على الأداء والتنفس الصحيح."
                val goalText = when {
                    (goal.toFloatOrNull() ?: w) < w -> "هدفك يتطلب التدرج في الحركة والعادات الغذائية؛ تجنب الحميات القاسية."
                    (goal.toFloatOrNull() ?: w) > w -> "هدفك زيادة الوزن؛ اهتم بوجبات متوازنة كافية وتمارين قوة تدريجية."
                    else -> "هدفك الحفاظ على الوزن؛ ركز على الاستمرارية والنوم والتوازن."
                }
                val days = listOf(
                    Triple("اليوم 1", "مشي مريح + إحماء", "ابدأ بـ $duration، بسرعة تسمح لك بالكلام دون لهاث شديد."),
                    Triple("اليوم 2", if (place == "الجيم") "قوة في الجيم" else "قوة منزلية خفيفة", strengthA),
                    Triple("اليوم 3", "استشفاء وحركة", "مشي خفيف أو إطالات مريحة دون إجبار المفاصل على مدى مؤلم."),
                    Triple("اليوم 4", "مشي تدريجي", "كرر المشي وزد بضع دقائق فقط إذا كان اليوم السابق مريحًا."),
                    Triple("اليوم 5", if (place == "الجيم") "قوة في الجيم" else "قوة منزلية خفيفة", strengthB),
                    Triple("اليوم 6", "نشاط اختياري", "مشي مريح أو نشاط تحبه، مع التوقف عند التعب غير المعتاد."),
                    Triple("اليوم 7", "راحة ومراجعة", "سجّل الوزن عند رغبتك وراجع الالتزام والنوم والطاقة.")
                )
                LazyColumn(Modifier.padding(pad).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 28.dp)) {
                    item {
                        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Navy)) {
                            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("خطة أسبوعك الأول", color = Teal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("مبنية على بياناتك الحالية", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                                Text("المكان: $place • الخبرة: $level • النشاط: $activity", color = Color.White)
                                Text("مؤشر كتلة الجسم التقريبي: " + if (bmi > 0f) String.format(Locale.US, "%.1f", bmi) else "غير متاح", color = Teal, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text(bmiLabel, color = Color.White)
                                Text(goalText, color = Color.White)
                            }
                        }
                    }
                    item { Text("الجدول المقترح", fontSize = 21.sp, color = Navy, fontWeight = FontWeight.Bold) }
                    items(days) { day -> PlanDayCard(day.first, day.second, day.third) { addLog("خطة", day.first + " — " + day.second, "تم تسجيل إنجاز اليوم") } }
                    item {
                        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE4F7F3))) {
                            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("كيف تتدرج؟", color = Navy, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("إذا أنهيت الأسبوع دون ألم أو إجهاد غير معتاد، زد المدة تدريجيًا في الأسبوع التالي. لا تحتاج إلى الجري أو إلى أدوات رياضية للبدء.")
                                Text("ملاحظة: هذه بداية عامة تتكيف مع مستوى النشاط والخبرة والمكان، وليست تشخيصًا أو وصفة طبية. مؤشر كتلة الجسم أداة فرز تقريبية وليس حكمًا كاملًا على الصحة.", color = Color.DarkGray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            "دليل التمارين" -> LazyColumn(Modifier.padding(pad).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
                item { Text("مكتبة التمارين", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy); Text("اختَر تمرينًا مناسبًا لمستواك. ابدأ بهدوء، وتوقف عند الألم الحاد أو الدوخة.", color = Color.Gray) }
                item { ExerciseGuideCard("المشي في المكان", "إحماء • مبتدئ • بدون أدوات", Icons.Default.DirectionsWalk, "1) قف باستقامة وارخِ الكتفين.  2) ارفع الركبتين بالتبادل.  3) حرّك الذراعين بصورة طبيعية.  4) حافظ على تنفس منتظم.", "ابدأ بـ 3–5 دقائق، ثم زد تدريجيًا حسب قدرتك.", "الانحناء للأمام، حبس النفس، أو رفع الركبتين بعنف.") { addLog("تمرين", "المشي في المكان", "تم تسجيل التمرين من مكتبة التمارين") } }
                item { ExerciseGuideCard("الجلوس والوقوف من كرسي", "قوة الرجلين • مبتدئ • كرسي ثابت", Icons.Default.AccessibilityNew, "1) ضع الكرسي خلفك على أرض ثابتة.  2) القدمان بعرض الكتفين.  3) ادفع الوركين للخلف وانزل ببطء.  4) المس الكرسي ثم قف مع الزفير.", "ابدأ بـ 1–2 مجموعة، كل مجموعة 6–10 تكرارات، مع راحة 60–90 ثانية.", "ترك الركبتين تنهاران للداخل، أو السقوط على الكرسي بسرعة.") { addLog("تمرين", "الجلوس والوقوف من كرسي", "تم تسجيل التمرين من مكتبة التمارين") } }
                item { ExerciseGuideCard("ضغط الحائط", "الجزء العلوي • مبتدئ • حائط", Icons.Default.FitnessCenter, "1) ضع راحتي اليدين على الحائط بمستوى الصدر.  2) ارجع للخلف خطوة أو خطوتين.  3) اثنِ المرفقين واقترب بجسمك كقطعة واحدة.  4) ادفع الحائط للعودة.", "ابدأ بـ 1–2 مجموعة، كل مجموعة 6–10 تكرارات.", "تقويس أسفل الظهر، أو إبعاد المرفقين كثيرًا عن الجسم.") { addLog("تمرين", "ضغط الحائط", "تم تسجيل التمرين من مكتبة التمارين") } }
                item { ExerciseGuideCard("رفع الكعبين", "توازن وربلة الساق • مبتدئ • كرسي للدعم", Icons.Default.Accessibility, "1) قف خلف كرسي ثابت وامسكه بخفة.  2) ارفع الكعبين ببطء.  3) اثبت لحظة.  4) انزل بتحكم دون ارتداد.", "ابدأ بـ 2 مجموعة من 8–12 تكرارًا.", "التأرجح أو الاعتماد الكامل على اليدين بدل عضلات الساق.") { addLog("تمرين", "رفع الكعبين", "تم تسجيل التمرين من مكتبة التمارين") } }
                item { Text("قاعدة التدرج", color = Navy, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                item { Text("نفّذ التمرين بجودة قبل زيادة التكرارات. اترك يوم راحة عند الحاجة، واستشر مختصًا إذا كانت لديك حالة صحية أو إصابة تؤثر على الحركة.", color = Color.Gray) }
            }
            "دليل الوجبات" -> LazyColumn(Modifier.padding(pad).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
                item { Text("دليل الأكل المصري", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy); Text("أمثلة مرئية مبسطة لبناء وجبة متوازنة. الكميات والسعرات الفعلية تختلف حسب الوصفة وحجم الحصة.", color = Color.Gray) }
                item { MealGuideCard("فطار متوازن", "بيض + عيش بلدي + خضار", Icons.Default.Restaurant, "بيض مسلوق أو مطهو بقليل من الزيت، مع حصة مناسبة من العيش البلدي وخيار وطماطم.", "يمكن استبدال البيض بالفول، مع ضبط الزيت والعيش حسب هدفك.") }
                item { MealGuideCard("غداء منزلي", "فراخ أو سمك + أرز + سلطة", Icons.Default.Restaurant, "اجعل نصف الطبق خضارًا، وربعًا مصدر بروتين، والربع المتبقي أرزًا أو بطاطس أو حبوبًا.", "الشوي أو الطهي بالفرن يقلل الحاجة للزيت مقارنة بالقلي العميق.") }
                item { MealGuideCard("وجبة نباتية", "فول أو عدس + سلطة + عيش", Icons.Default.Spa, "البقول مصدر بروتين وألياف. انتبه لكمية الزيت والإضافات، وأضف خضارًا لزيادة التنوع.", "نوّع مصادر البروتين خلال اليوم واشرب الماء بانتظام.") }
                item { MealGuideCard("سناك بسيط", "فاكهة + زبادي طبيعي", Icons.Default.LocalCafe, "اختيار بسيط بين الوجبات حسب الجوع واحتياجك اليومي.", "اختر كمية مناسبة، ولا تحتاج إلى تناول سناك إذا لم تكن جائعًا.") }
                item { Text("مهم: هذه أمثلة إرشادية وليست حسابًا للسعرات أو خطة علاجية. سنربط الكميات بخطة المستخدم عند إضافة محرك التغذية وقاعدة بيانات موثوقة.", color = Color.Gray) }
            }
            "النشاط" -> LazyColumn(Modifier.padding(pad).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                item { Text("المشي والحركة", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy); Text("سجل النشاط والمدة. عداد الخطوات التلقائي قيد التطوير.", color = Color.Gray) }
                item { Metric("الخطوات المسجلة", steps.toString(), Icons.Default.DirectionsWalk, Modifier.fillMaxWidth()) }
                item { Button(onClick = { steps += 500; p.edit().putInt("steps", steps).apply() }, modifier = Modifier.fillMaxWidth()) { Text("إضافة 500 خطوة") } }
                item { ActivityCard("المشي", walk, listOf(10, 20, 30)) { walk += it; p.edit().putInt("walk", walk).apply(); addLog("مشي", "مشي لمدة " + it + " دقيقة") } }
                item { ActivityCard("الجري", run, listOf(5, 10, 15)) { run += it; p.edit().putInt("run", run).apply(); addLog("جري", "جري لمدة " + it + " دقيقة") } }
                item { Button(onClick = { entryType = "تمرين"; dialog = true }, modifier = Modifier.fillMaxWidth()) { Text("تسجيل تمرين منزلي") } }
            }
            "الوجبات" -> LazyColumn(Modifier.padding(pad).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { Text("الوجبات والتغذية", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy); Text("سجل الأكل المصري المنزلي والكميات والملاحظات.", color = Color.Gray) }
                item { Button(onClick = { entryType = "وجبة"; dialog = true }, modifier = Modifier.fillMaxWidth()) { Text("إضافة وجبة") } }
                val mealLogs = logs.filter { it.contains("|وجبة|") }
                if (mealLogs.isEmpty()) item { Text("لم تسجل وجبات بعد.", color = Color.Gray) }
                items(mealLogs) { LogItem(it) }
            }
            "التقدم" -> LazyColumn(Modifier.padding(pad).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { Text("متابعة التقدم", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy) }
                item { Field("الوزن الجديد بالكجم", newWeight, { newWeight = it }, true) }
                item { Button(onClick = { val v = newWeight.toFloatOrNull(); if (v != null && v in 30f..350f) { weight = v.toString(); p.edit().putString("weight", weight).apply(); addLog("وزن", "تسجيل الوزن", weight + " كجم") } }, modifier = Modifier.fillMaxWidth()) { Text("حفظ الوزن") } }
                item { Metric("الوزن الحالي", weight + " كجم", Icons.Default.MonitorWeight, Modifier.fillMaxWidth()) }
                items(logs.filter { it.contains("|وزن|") }) { LogItem(it) }
            }
        }
    }
    if (dialog) AlertDialog(
        onDismissRequest = { dialog = false },
        title = { Text("إضافة " + entryType) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(entryName, { entryName = it }, label = { Text("اسم الطعام أو التمرين") }, singleLine = true)
            OutlinedTextField(entryNote, { entryNote = it }, label = { Text("الكمية أو الملاحظات") })
            Text("السعرات الغذائية ستحتاج قاعدة بيانات غذائية موثوقة في مرحلة لاحقة.", color = Color.Gray, fontSize = 12.sp)
        } },
        confirmButton = { Button(onClick = { if (entryName.isNotBlank()) { addLog(entryType, entryName, entryNote); entryName = ""; entryNote = ""; dialog = false } }) { Text("حفظ") } },
        dismissButton = { TextButton(onClick = { dialog = false }) { Text("إلغاء") } }
    )
}

@Composable
private fun Field(label: String, value: String, change: (String) -> Unit, number: Boolean = false) {
    OutlinedTextField(value, change, label = { Text(label) }, modifier = Modifier.fillMaxWidth(), singleLine = true,
        keyboardOptions = if (number) KeyboardOptions(keyboardType = KeyboardType.Decimal) else KeyboardOptions.Default)
}

@Composable
private fun SelectField(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(selected, modifier = Modifier.weight(1f)); Icon(Icons.Default.ArrowDropDown, null) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { onSelect(it); expanded = false }) }
            }
        }
    }
}

@Composable
private fun Metric(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = Teal, modifier = Modifier.size(25.dp))
            Text(value, color = Navy, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Text(title, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ActivityCard(title: String, total: Int, options: List<Int>, onAdd: (Int) -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontSize = 18.sp, color = Navy, fontWeight = FontWeight.Bold)
            Text("إجمالي مسجل: " + total + " دقيقة", color = Color.Gray)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { n -> OutlinedButton(onClick = { onAdd(n) }, modifier = Modifier.weight(1f)) { Text(n.toString() + " د") } }
            }
        }
    }
}

@Composable
private fun LogItem(line: String) {
    val parts = line.split("|")
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Favorite, null, tint = Teal, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(parts.getOrNull(2) ?: line, color = Navy, fontWeight = FontWeight.SemiBold)
                Text((parts.getOrNull(1) ?: "") + " • " + (parts.getOrNull(0) ?: ""), color = Color.Gray, fontSize = 12.sp)
                if (!parts.getOrNull(3).isNullOrBlank()) Text(parts[3], color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}


@Composable
private fun ExerciseGuideCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, steps: String, dosage: String, mistakes: String, onLog: () -> Unit) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(76.dp).background(Color(0xFFE4F7F3), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Color(0xFF087F73), modifier = Modifier.size(44.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Navy)
                    Text(subtitle, color = Color.Gray, fontSize = 12.sp)
                }
            }
            HorizontalDivider()
            Text("طريقة الأداء", color = Navy, fontWeight = FontWeight.Bold)
            Text(steps)
            Text("الجرعة المقترحة", color = Navy, fontWeight = FontWeight.Bold)
            Text(dosage)
            Text("أخطاء شائعة", color = Color(0xFF9A4A20), fontWeight = FontWeight.Bold)
            Text(mistakes)
            OutlinedButton(onClick = onLog, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.CheckCircle, null); Text("  تسجيل هذا التمرين") }
        }
    }
}

@Composable
private fun MealGuideCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, description: String, tip: String) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(76.dp).background(Color(0xFFFFF0D9), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Color(0xFF9A5A00), modifier = Modifier.size(42.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Navy)
                    Text(subtitle, color = Color.Gray, fontSize = 12.sp)
                }
            }
            Text(description)
            Text("اقتراح عملي", color = Navy, fontWeight = FontWeight.Bold)
            Text(tip, color = Color.DarkGray)
        }
    }
}


@Composable
private fun PlanDayCard(day: String, title: String, details: String, onDone: () -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(day, color = Teal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(title, color = Navy, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(details, color = Color.DarkGray)
            OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.CheckCircle, null); Text("  تسجيل إنجاز اليوم")
            }
        }
    }
}
