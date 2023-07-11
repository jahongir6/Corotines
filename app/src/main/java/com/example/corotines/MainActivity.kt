package com.example.corotines

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.corotines.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*//corotinlar oqimlrning ornini egallamaydilar balki ularni boshqaruvchi fremwork dir
       // corotinlarni boshlash uchun ikkita funksiyasi bor
       // 1->launch bu ->hechnarsa qaytarmaydi.boshqacha qilib aytganda ishga tushuradi va unutadi
       //2->async bu -> qiymat qaytaradi.boshqacha qilib aytganda vazifani bajaradi va natijani qaytaradi
       //withContext bu ->await orniga qollaniladiga funksiya*/
/*eslatma:
1.parallel ish bajarishda ehtiyoj bolganda withContext dan  foydalaning.
2.qachonki parallel muhit kerak bolsa async ni ishlating.
3.launch bilan olinishi mumkunn bolgan natijani withContext va async ishlatish orqai olinadi
4.async ishlatilishi bilan esa bir nechta vazifalar natijalarni parallel muhitda olinadi
*/
/*dispatcherlar -> ish bajarilishi kerak bolgan oqimni tanlashda kotoutinlarga yordam beradi*/
/*0.Scope larni kotlin korotinlarda shunchalik foydaliki biz ularni activity destroy (toxtatishi)
* bolishi bilanorqa fonda ishlab turgan vazifalarni bekor qilish uchun  ishlatamiz.
* 1.globalScope da activity destroy bolganda ham funksiyalar ishlashda davom etadi
* 2.coroutineScope har qanday ichki funksiyasi xatolikka uchraganida  uni bekor qiladi
* agar biz xatolikka uchraganida taqdirda ham boshqa vazifalarni davom ettirishni xoxlasak,
* biz supervisorScope bilan ishlaymiz.supervisorScope ichki funkksiyalari catolikka uchraganida
* unibekor qiilmaydi
*
* */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnCalculation.setOnClickListener {
            // binding.btnCalculation.isEnabled = false  bu -> kod buttonni bosaganingizdan keyin bosa olmaysiz natija kelganda  keyin bosa olasiz natija 3 sekunnda keladi chunki pasda 3 sekunda kelsin deb ytib qoydim
            GlobalScope.launch {//globalscope.launch qilganimni sababi launch qiymat qaytarmaydi
                val result = testHealthy(
                    calculation(
                        binding.tvBoy.text.toString().toInt(),
                        binding.tvMassa.text.toString().toInt()
                    )
                )
                binding.tvInfo.text = result
                // binding.btnCalculation.isEnabled = true bu natija kegandan keyin buttonni yana faolashtirib qoydim
            }
        }

    }

    //bu yerda globalScope.async qilgannimdi sababi qiymat qaytaradi async shunning uchun
    // va Dispatcher.default qilganimdi sababi default thread hisob kitoblarni ammalga oshiradi
    suspend fun calculation(boyi: Int, massa: Int): Double {
        return GlobalScope.async(Dispatchers.Default) {
            var BMI = (massa.toDouble() * 10000) / (boyi * boyi.toDouble())
            BMI
        }.await()
    }

    //bu yerda globalScope.async qilgannimdi sababi qiymat qaytaradi async shunning uchun
    // va Dispatcher.default qilganimdi sababi io thread internetdan malumotlar
    // kutubxonalar olib kelish uchun alida lekin hozir kichkina loyixa bolgani uchun
    // io thread bolaveradi aslida defauult thread dan foydalanish kerak
    suspend fun testHealthy(bmi: Double): String {
        return GlobalScope.async(Dispatchers.IO) {
            Thread.sleep(3000)//bu natija ekranga nechchi sekunda chiqishini qoydim bu shart ham emas
            if (bmi < 18.5) {
                // //globalscope.launch qilganimni sababi launch qiymat qaytarmaydi
                GlobalScope.launch(Dispatchers.Main) {
                    binding.root.setBackgroundColor(Color.BLUE)
                }
                "past vazn"
            } else if (bmi < 25.0) {
                GlobalScope.launch(Dispatchers.Main) {
                    binding.root.setBackgroundColor(Color.GREEN)
                }
                "normal"
            } else if (bmi < 30.0) {
                GlobalScope.launch(Dispatchers.Main) {
                    binding.root.setBackgroundColor(Color.YELLOW)
                }
                "semiz"
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                    binding.root.setBackgroundColor(Color.RED)
                }
                "no soglom"
            }

        }.await()
    }
}