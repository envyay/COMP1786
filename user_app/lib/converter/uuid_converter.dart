import 'package:json_annotation/json_annotation.dart';
import 'package:uuid/uuid.dart';
import 'dart:typed_data';

String convertBitsToUuid(int msb, int lsb) {
  final bytes = Uint8List(16);
  final bd = ByteData.view(bytes.buffer);

  bd.setInt64(0, msb);
  bd.setInt64(8, lsb);

  return Uuid.unparse(bytes);
}

Map<String, int> uuidToBits(String uuidString) {
  String hex = uuidString.replaceAll('-', '');
  String msbHex = hex.substring(0, 16);
  String lsbHex = hex.substring(16);
  BigInt msbBig = BigInt.parse(msbHex, radix: 16);
  BigInt lsbBig = BigInt.parse(lsbHex, radix: 16);
  int msb = msbBig.toSigned(64).toInt();
  int lsb = lsbBig.toSigned(64).toInt();

  return {
    'mostSignificantBits': msb,
    'leastSignificantBits': lsb,
  };
}

class UuidConverter extends JsonConverter<String?, Map<String, dynamic>?> {
  const UuidConverter();

  @override
  String? fromJson(Map<String, dynamic>? json) {
    if (json == null) return null;
    final uuid = convertBitsToUuid(json['mostSignificantBits'] as int, json['leastSignificantBits'] as int);
    return uuid;
  }

  @override
  Map<String, dynamic>? toJson(String? object) {
    if (object == null) return null;
    return uuidToBits(object);
  }
}