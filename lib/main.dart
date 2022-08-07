import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = MethodChannel('com.izam.dev/print_service');
  String _batteryLevel = "Unknown battery level";
  dynamic _bluetoothPrinters;

  Future<void> _getBatteryLevel() async {
    String batteryLevel;
    try {
      final int result = await platform.invokeMethod('getBatteryLevel');
      batteryLevel = 'Battery level at $result %.';
    } on PlatformException catch (e) {
      batteryLevel = "Failed to get battery level: '${e.message}'.";
    }

    setState(() {
      _batteryLevel = batteryLevel;
    });
  }

  Future<dynamic> _getBluetoothPrinters() async {
    dynamic bluetoothPrinters;
    List<Map<String, dynamic>> bluetoothPrintersInfo;

    try {
      bluetoothPrinters = await platform.invokeMethod('getBluetoothPrinters');
      print(
          "flutter main.dart _getBluetoothPrinters | bluetoothPrinters: ${bluetoothPrinters.toString()}");
    } on PlatformException catch (e) {
      print("Failed to get bluetooth printers list: '${e.message}'.");
    }

    setState(() {
      _bluetoothPrinters = bluetoothPrinters;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            InkWell(
              onTap: () {
                _getBluetoothPrinters();
              },
              child: Container(
                color: Colors.green.withOpacity(0.5),
                width: 200,
                height: 75,
                child: const Center(child: Text('Get bluetooth printers')),
              ),
            ),
            if (_bluetoothPrinters != null && _bluetoothPrinters is List)
              _bluetoothPrinters
                  .map((printer) => Text(printer.toString()))
                  .toList(),
          ],
        ),
      ),
    );
  }
}
