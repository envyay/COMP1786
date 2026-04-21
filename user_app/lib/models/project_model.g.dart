// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'project_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ProjectModel _$ProjectModelFromJson(Map<String, dynamic> json) => ProjectModel(
  id: const UuidConverter().fromJson(json['id'] as Map<String, dynamic>?),
  budget: (json['budget'] as num?)?.toDouble(),
  departmentInformation: json['departmentInformation'] as String?,
  description: json['description'] as String?,
  manager: json['manager'] as String?,
  name: json['name'] as String?,
  othersList: json['othersList'] as String?,
  specialRequirements: json['specialRequirements'] as String?,
  endDate: const TimestampConverter().fromJson(json['endDate'] as Timestamp?),
  startDate: const TimestampConverter().fromJson(
    json['startDate'] as Timestamp?,
  ),
  status: json['status'] as String?,
);

Map<String, dynamic> _$ProjectModelToJson(ProjectModel instance) =>
    <String, dynamic>{
      'id': const UuidConverter().toJson(instance.id),
      'budget': instance.budget,
      'departmentInformation': instance.departmentInformation,
      'description': instance.description,
      'manager': instance.manager,
      'name': instance.name,
      'othersList': instance.othersList,
      'specialRequirements': instance.specialRequirements,
      'endDate': const TimestampConverter().toJson(instance.endDate),
      'startDate': const TimestampConverter().toJson(instance.startDate),
      'status': instance.status,
    };
