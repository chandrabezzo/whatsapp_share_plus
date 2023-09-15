package com.solusibejo.whatsapp_share_plus

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.core.content.FileProvider
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File

/** WhatsappShare  */
class WhatsappShare : FlutterPlugin, MethodCallHandler {
    private var context: Context? = null
    private var methodChannel: MethodChannel? = null
    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        onAttachedToEngine(binding.applicationContext, binding.binaryMessenger)
    }

    private fun onAttachedToEngine(applicationContext: Context, messenger: BinaryMessenger) {
        context = applicationContext
        methodChannel = MethodChannel(messenger, "whatsapp_share_plus")
        methodChannel!!.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        context = null
        methodChannel!!.setMethodCallHandler(null)
        methodChannel = null
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        if (call.method == "shareFile") {
            shareFile(call, result)
        } else if (call.method == "share") {
            share(call, result)
        } else if (call.method == "isInstalled") {
            isInstalled(call, result)
        } else {
            result.notImplemented()
        }
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun isInstalled(call: MethodCall, result: MethodChannel.Result) {
        try {
            val packageName = call.argument<String>("package")
            if (packageName == null || packageName.isEmpty()) {
                Log.println(Log.ERROR, "", "FlutterShare Error: Package name null or empty")
                result.error("FlutterShare:Package name cannot be null or empty", null, null)
                return
            }
            val pm = context!!.packageManager
            val isInstalled = isPackageInstalled(packageName, pm)
            result.success(isInstalled)
        } catch (ex: Exception) {
            Log.println(Log.ERROR, "", "FlutterShare: Error")
            result.error(ex.message!!, null, null)
        }
    }

    private fun share(call: MethodCall, result: MethodChannel.Result) {
        try {
            val title = call.argument<String>("title")
            val text = call.argument<String>("text")
            val linkUrl = call.argument<String>("linkUrl")
            val chooserTitle = call.argument<String>("chooserTitle")
            val phone = call.argument<String>("phone")
            val packageName = call.argument<String>("package")
            if (title == null || title.isEmpty()) {
                Log.println(Log.ERROR, "", "FlutterShare Error: Title null or empty")
                result.error("FlutterShare: Title cannot be null or empty", null, null)
                return
            } else if (phone == null || phone.isEmpty()) {
                Log.println(Log.ERROR, "", "FlutterShare Error: phone null or empty")
                result.error("FlutterShare: phone cannot be null or empty", null, null)
                return
            } else if (packageName == null || packageName.isEmpty()) {
                Log.println(Log.ERROR, "", "FlutterShare Error: Package name null or empty")
                result.error("FlutterShare:Package name cannot be null or empty", null, null)
                return
            }
            val extraTextList = ArrayList<String?>()
            if (text != null && !text.isEmpty()) {
                extraTextList.add(text)
            }
            if (linkUrl != null && !linkUrl.isEmpty()) {
                extraTextList.add(linkUrl)
            }
            var extraText: String? = ""
            if (!extraTextList.isEmpty()) {
                extraText = TextUtils.join("\n\n", extraTextList)
            }
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.setPackage(packageName)
            intent.putExtra("jid", "$phone@s.whatsapp.net")
            intent.putExtra(Intent.EXTRA_SUBJECT, title)
            intent.putExtra(Intent.EXTRA_TEXT, extraText)

            //Intent chooserIntent = Intent.createChooser(intent, chooserTitle);
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context!!.startActivity(intent)
            result.success(true)
        } catch (ex: Exception) {
            Log.println(Log.ERROR, "", "FlutterShare: Error")
            result.error(ex.message!!, null, null)
        }
    }

    private fun shareFile(call: MethodCall, result: MethodChannel.Result) {
        var filePaths: ArrayList<String?>? = ArrayList()
        val files = ArrayList<Uri>()
        try {
            val title = call.argument<String>("title")
            val text = call.argument<String>("text")
            filePaths = call.argument("filePath")
            val chooserTitle = call.argument<String>("chooserTitle")
            val phone = call.argument<String>("phone")
            val packageName = call.argument<String>("package")
            if (filePaths == null || filePaths.isEmpty()) {
                Log.println(
                    Log.ERROR,
                    "",
                    "FlutterShare: ShareLocalFile Error: filePath null or empty"
                )
                result.error("FlutterShare: FilePath cannot be null or empty", null, null)
                return
            } else if (phone == null || phone.isEmpty()) {
                Log.println(Log.ERROR, "", "FlutterShare Error: phone null or empty")
                result.error("FlutterShare: phone cannot be null or empty", null, null)
                return
            } else if (packageName == null || packageName.isEmpty()) {
                Log.println(Log.ERROR, "", "FlutterShare Error: Package name null or empty")
                result.error("FlutterShare:Package name cannot be null or empty", null, null)
                return
            }
            for (i in filePaths.indices) {
                val file = File(filePaths[i])
                val fileUri = FileProvider.getUriForFile(
                    context!!,
                    context!!.applicationContext.packageName + ".provider",
                    file
                )
                files.add(fileUri)
            }
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.action = Intent.ACTION_SEND_MULTIPLE
            intent.type = "*/*"
            intent.setPackage(packageName)
            intent.putExtra("jid", "$phone@s.whatsapp.net")
            intent.putExtra(Intent.EXTRA_SUBJECT, title)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.putExtra(Intent.EXTRA_STREAM, files)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            //Intent chooserIntent = Intent.createChooser(intent, chooserTitle);
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context!!.startActivity(intent)
            result.success(true)
        } catch (ex: Exception) {
            result.error(ex.message!!, null, null)
            Log.println(Log.ERROR, "", "FlutterShare: Error")
        }
    }

    companion object {
        /** Plugin registration.  */
        @Suppress("deprecation")
        fun registerWith(registrar: Registrar) {
            val instance = WhatsappShare()
            instance.onAttachedToEngine(registrar.context(), registrar.messenger())
        }
    }
}