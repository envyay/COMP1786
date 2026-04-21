import 'package:flutter/material.dart';
import 'package:user_app/models/project_model.dart';
import 'package:user_app/pages/project_detail_page.dart';
import 'package:user_app/repositories/project_repo.dart';
import 'package:user_app/widgets/project_card.dart';

class ProjectsPage extends StatelessWidget {
  const ProjectsPage({super.key});

  @override
  Widget build(BuildContext context) {
    final projectRepo = ProjectRepo();

    return Scaffold(
      appBar: AppBar(title: const Text('Projects')),
      body: FutureBuilder<List<ProjectModel>>(
        future: projectRepo.getProjects(),
        builder: (context, snapshot) {
          if (snapshot.hasError) {
            return Text(snapshot.error.toString());
          }

          if (snapshot.connectionState == ConnectionState.done) {
            final projects = snapshot.data ?? [];
            return _buildList(projects: projects);
          }

          return Text('Loading');
        },
      ),
    );
  }

  Widget _buildList({required List<ProjectModel> projects}) {
    return ListView.builder(
      itemCount: projects.length,
      itemBuilder: (context, index) {
        final project = projects[index];
        return ProjectCard(
          model: project,
          onTap: () {
            Navigator.of(context).push(
              MaterialPageRoute(
                builder: (context) {
                  return ProjectDetailPage(model: project);
                },
              ),
            );
          },
        );
      },
    );
  }
}
