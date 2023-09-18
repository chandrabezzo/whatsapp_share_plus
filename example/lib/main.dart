import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:path_provider/path_provider.dart';
import 'package:whatsapp_share_plus/whatsapp_share_plus.dart';

void main() => runApp(MyApp());

// ignore: must_be_immutable
class MyApp extends StatelessWidget {
  final scaffoldKey = GlobalKey<ScaffoldState>();
  File? _image;

  final String _phone = '6281546415204';

  MyApp({super.key});

  Future<void> share() async {
    await WhatsappShare.share(
      text: 'Example share text',
      linkUrl: 'https://flutter.dev/',
      phone: _phone,
    );
  }

  Future<void> shareFile() async {
    await getImage();
    Directory? directory = await getExternalStorageDirectory();

    debugPrint('${directory?.path} / ${_image?.path}');
    if (_image != null) {
      await WhatsappShare.shareFile(
        phone: _phone,
        text: 'Testing',
        filePath: _image!.path,
      );
    }
  }

  void isInstalled(BuildContext context) async {
    final val = await WhatsappShare.isInstalled();
    debugPrint('Is installed; $val');
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        key: scaffoldKey,
        appBar: AppBar(
          title: const Text('Whatsapp Share'),
        ),
        body: Center(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              ElevatedButton(
                onPressed: share,
                child: const Text('Share text and link'),
              ),
              ElevatedButton(
                onPressed: shareFile,
                child: const Text('Share Image'),
              ),
              ElevatedButton(
                onPressed: () => isInstalled(context),
                child: const Text('is Installed'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  ///Pick Image From gallery using image_picker plugin
  Future getImage() async {
    try {
      final ImagePicker picker = ImagePicker();
      XFile? pickedFile = (await picker.pickImage(source: ImageSource.gallery));

      if (pickedFile != null) {
        // getting a directory path for saving
        final directory = await getExternalStorageDirectory();

        // copy the file to a new path
        await pickedFile.saveTo('${directory?.path}/image1.png');
        _image = File('${directory?.path}/image1.png');
      }
    } catch (er) {
      debugPrint(er.toString());
    }
  }
}
