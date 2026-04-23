// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'expense_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ExpenseModel _$ExpenseModelFromJson(Map<String, dynamic> json) => ExpenseModel(
  id: const UuidConverter().fromJson(json['id'] as Map<String, dynamic>?),
  amount: (json['amount'] as num?)?.toDouble(),
  claimant: json['claimant'] as String?,
  currency: json['currency'] as String?,
  date: const TimestampConverter().fromJson(json['date'] as Timestamp?),
  location: json['location'] as String?,
  paymentMethod: (json['paymentMethod'] as num?)?.toInt(),
  paymentStatus: (json['paymentStatus'] as num?)?.toInt(),
  projectId: const UuidConverter().fromJson(
    json['projectId'] as Map<String, dynamic>?,
  ),
  type: json['type'] as String?,
);

Map<String, dynamic> _$ExpenseModelToJson(ExpenseModel instance) =>
    <String, dynamic>{
      'id': const UuidConverter().toJson(instance.id),
      'amount': instance.amount,
      'claimant': instance.claimant,
      'currency': instance.currency,
      'date': const TimestampConverter().toJson(instance.date),
      'location': instance.location,
      'paymentMethod': instance.paymentMethod,
      'paymentStatus': instance.paymentStatus,
      'projectId': const UuidConverter().toJson(instance.projectId),
      'type': instance.type,
    };
