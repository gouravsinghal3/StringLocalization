package com.example.mytestapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposables
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import java.io.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
	private var disposable = Disposables.disposed()
	private var translationList = ArrayList<Array<String>>()
	private lateinit var labelTxt: AppCompatTextView
	companion object {
		private const val FILE_NAME = "TestCsv.csv"
		private const val URL = "https://drive.google.com/uc?export=download&id=1OuPzABSoHop6E2U0IvVjiR2cqCV5JITy"
	}
	private val fileDownloader by lazy {
		FileDownloader(
			OkHttpClient.Builder().build()
		)
	}
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		labelTxt = findViewById(R.id.tvHelloWorld)
		downloadFile()
	}

	private fun downloadFile(){
		RxJavaPlugins.setErrorHandler {
			Log.e("Error", it.localizedMessage)
		}

		val targetFile = File(cacheDir, FILE_NAME)

		disposable = fileDownloader.download(URL, targetFile)
			.throttleFirst(2, TimeUnit.SECONDS)
			.toFlowable(BackpressureStrategy.LATEST)
			.subscribeOn(Schedulers.io())
			.observeOn(mainThread())
			.subscribe({
				           Toast.makeText(this, "$it% Downloaded", Toast.LENGTH_SHORT).show()
			           }, {
				           Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
			           }, {
				           Toast.makeText(this, "Complete Downloaded", Toast.LENGTH_SHORT).show()
				           readCSVFile(targetFile)
			           })

	}

	private fun readCSVFile(fileName : File){

		try {
			var br = BufferedReader( FileReader(fileName));
					br.forEachLine {
						val tokens = it.split(",");
						translationList.add(tokens.toTypedArray())
					}

			clickListner()
		}
	 catch (e : FileNotFoundException) {
		e.printStackTrace();
	} catch (e : IOException) {
		e.printStackTrace();
	}
	}

	private fun clickListner(){
		if(!translationList.isNullOrEmpty()) {
			findViewById<Button>(R.id.btnEnglish).setOnClickListener {
                           labelTxt.setText(translationList.get(0)[0])
			}
			findViewById<Button>(R.id.btnHindi).setOnClickListener {
				labelTxt.setText(translationList[0][1])
			}
			findViewById<Button>(R.id.btnChinese).setOnClickListener {
				labelTxt.setText(translationList[0][2])
			}
			findViewById<Button>(R.id.btnKJapanese).setOnClickListener {
				labelTxt.setText(translationList[0][3])
			}
			findViewById<Button>(R.id.btnRussian).setOnClickListener {
				labelTxt.setText(translationList[0][4])
			}
			findViewById<Button>(R.id.btnSpanish).setOnClickListener {
				labelTxt.setText(translationList[0][5])
			}
		}
	}

}