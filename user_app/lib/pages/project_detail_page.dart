import 'package:flutter/material.dart';
import 'package:user_app/models/project_model.dart';

import 'expenses_page.dart';

class ProjectDetailPage extends StatelessWidget {
  const ProjectDetailPage({super.key, required this.model});

  final ProjectModel model;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(model.name ?? '')),
      bottomNavigationBar: Container(
        padding: .symmetric(horizontal: 16, vertical: 8),
        child: FilledButton(
          onPressed: () {
            Navigator.of(context).push(
              MaterialPageRoute(
                builder: (context) {
                  return ExpensesPage(projectId: model.id ?? '');
                },
              ),
            );
          },
          child: Text('View Expenses'),
        ),
      ),
      body: Container(
        padding: .symmetric(horizontal: 16),
        child: ListView(
          children: [
            _buildLabel(label: 'Project name: ', value: model.name ?? ''),
            _buildLabel(label: 'Manager: ', value: model.manager ?? ''),
            _buildLabel(label: 'Budget: ', value: model.budget.toString()),
            _buildLabel(label: 'Description: ', value: model.description ?? ''),
            _buildLabel(label: 'Others List: ', value: model.othersList ?? ''),
            _buildLabel(
              label: 'Special Requirements: ',
              value: model.specialRequirements ?? '',
            ),
            _buildLabel(label: 'End Date: ', value: model.endDate.toString()),
            _buildLabel(
              label: 'Start Date: ',
              value: model.startDate.toString(),
            ),
            _buildLabel(label: 'Status: ', value: model.status ?? ''),
            _buildLabel(
              label: 'Department Information: ',
              value: model.departmentInformation ?? '',
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildLabel({required String label, required String value}) {
    return Container(
      margin: .symmetric(vertical: 8),
      child: RichText(
        text: TextSpan(
          text: label,
          style: TextStyle(
            fontSize: 20,
            fontWeight: .bold,
            color: Colors.black,
          ),
          children: [
            TextSpan(
              text: value,
              style: TextStyle(
                fontSize: 20,
                fontWeight: .normal,
                color: Colors.black,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
