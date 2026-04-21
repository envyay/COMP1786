import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:user_app/models/project_model.dart';

class ProjectRepo {
  final _projects  = FirebaseFirestore.instance.collection('projects');

  Future<List<ProjectModel>> getProjects() async {
    final result = await _projects.get();
    return result.docs.map((e) => ProjectModel.fromJson(e.data())).toList();
  }
}