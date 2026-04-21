import 'package:flutter/material.dart';
import 'package:user_app/models/project_model.dart';

class ProjectCard extends StatelessWidget {
  const ProjectCard({super.key, required this.model, required this.onTap});

  final ProjectModel model;
  final void Function() onTap;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: .symmetric(vertical: 8, horizontal: 16),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(8),
        border: .all(color: Colors.blueAccent, width: 1.5)
      ),
      child: Material(
        type: .transparency,
        child: InkWell(
          borderRadius: BorderRadius.circular(8),
          onTap: onTap,
          child: Container(
            padding: .symmetric(vertical: 8, horizontal: 16),
            child: Column(
              spacing: 8,
              crossAxisAlignment: .start,
              children: [
                _buildLabel('Project name: ${model.name ?? ''}'),
                _buildLabel('Manager: ${model.manager ?? ''}'),
                _buildLabel('Budget: ${model.budget ?? ''}'),
                _buildLabel('Status: ${model.status ?? ''}'),
                _buildLabel('Start Date: ${model.startDate ?? ''}'),
                _buildLabel('End Date: ${model.endDate ?? ''}'),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildLabel(String label) {
    return Text(label, style: TextStyle(color: Colors.black, fontSize: 12));
  }
}
