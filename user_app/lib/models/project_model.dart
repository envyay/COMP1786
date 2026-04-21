import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:user_app/converter/timestamp_converter.dart';
import 'package:user_app/converter/uuid_converter.dart';

part 'project_model.g.dart';

@JsonSerializable()
class ProjectModel {
  @UuidConverter() String? id;
  double? budget;
  String? departmentInformation;
  String? description;
  String? manager;
  String? name;
  String? othersList;
  String? specialRequirements;
  @TimestampConverter() DateTime? endDate;
  @TimestampConverter() DateTime? startDate;
  String? status;

  ProjectModel({
    this.id,
    this.budget,
    this.departmentInformation,
    this.description,
    this.manager,
    this.name,
    this.othersList,
    this.specialRequirements,
    this.endDate,
    this.startDate,
    this.status,
  });

  factory ProjectModel.fromJson(Map<String, dynamic> json) => _$ProjectModelFromJson(json);

  Map<String, dynamic> toJson() => _$ProjectModelToJson(this);
}
