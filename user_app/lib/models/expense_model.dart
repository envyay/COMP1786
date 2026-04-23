import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:user_app/converter/timestamp_converter.dart';
import 'package:user_app/converter/uuid_converter.dart';

part 'expense_model.g.dart';

@JsonSerializable()
class ExpenseModel {
  @UuidConverter() String? id;
  double? amount;
  String? claimant;
  String? currency;
  @TimestampConverter() DateTime? date;
  String? location;
  int? paymentMethod;
  int? paymentStatus;
  @UuidConverter() String? projectId;
  String? type;

  ExpenseModel({
    this.id,
    this.amount,
    this.claimant,
    this.currency,
    this.date,
    this.location,
    this.paymentMethod,
    this.paymentStatus,
    this.projectId,
    this.type,
  });

  factory ExpenseModel.fromJson(Map<String, dynamic> json) => _$ExpenseModelFromJson(json);

  Map<String, dynamic> toJson() => _$ExpenseModelToJson(this);
}
