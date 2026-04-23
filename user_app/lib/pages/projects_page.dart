import 'package:flutter/material.dart';
import 'package:user_app/models/project_model.dart';
import 'package:user_app/pages/project_detail_page.dart';
import 'package:user_app/repositories/project_repo.dart';
import 'package:user_app/widgets/project_card.dart';

class ProjectsPage extends StatefulWidget {
  const ProjectsPage({super.key});

  @override
  State<ProjectsPage> createState() => _ProjectsPageState();
}

class _ProjectsPageState extends State<ProjectsPage> {
  final projectRepo = ProjectRepo();
  List<ProjectModel> allProjects = [];
  List<ProjectModel> filteredProjects = [];
  final TextEditingController _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _searchController.addListener(_onSearchChanged);
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  void _onSearchChanged() {
    final query = _searchController.text.toLowerCase();
    setState(() {
      filteredProjects = allProjects
          .where((project) => (project.name?.toLowerCase() ?? "").contains(query))
          .toList();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Projects'),
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(60),
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: TextField(
              controller: _searchController,
              decoration: InputDecoration(
                hintText: 'Search project name...',
                prefixIcon: const Icon(Icons.search),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                filled: true,
                fillColor: Colors.white,
                contentPadding: const EdgeInsets.symmetric(vertical: 0),
              ),
            ),
          ),
        ),
      ),
      body: FutureBuilder<List<ProjectModel>>(
        future: projectRepo.getProjects(),
        builder: (context, snapshot) {
          if (snapshot.hasError) return Center(child: Text("${snapshot.error}"));

          if (snapshot.connectionState == ConnectionState.done) {
            if (allProjects.isEmpty && snapshot.hasData) {
              allProjects = snapshot.data!;
              if (_searchController.text.isEmpty) {
                filteredProjects = allProjects;
              } else {
                _onSearchChanged();
              }
            }

            return _buildList(projects: filteredProjects);
          }

          return const Center(child: CircularProgressIndicator());
        },
      ),
    );
  }

  Widget _buildList({required List<ProjectModel> projects}) {
    if (projects.isEmpty) {
      return const Center(child: Text("No project names match your search."));
    }
    return ListView.builder(
      itemCount: projects.length,
      itemBuilder: (context, index) {
        final project = projects[index];
        return ProjectCard(
          model: project,
          onTap: () {
            Navigator.of(context).push(
              MaterialPageRoute(
                builder: (context) => ProjectDetailPage(model: project),
              ),
            );
          },
        );
      },
    );
  }
}