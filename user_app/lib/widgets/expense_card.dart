import 'package:flutter/material.dart';
import 'package:user_app/contants/contants.dart';

import '../models/expense_model.dart';

class ExpenseCard extends StatelessWidget {
  const ExpenseCard({super.key, required this.model});

  final ExpenseModel model;

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
          child: Container(
            padding: .symmetric(vertical: 8, horizontal: 16),
            child: Column(
              spacing: 8,
              crossAxisAlignment: .start,
              children: [
                _buildLabel('Expense Type: ${model.type ?? ''}'),
                _buildLabel('Budget: ${model.budget ?? ''}'),
                _buildLabel('Claimant: ${model.claimant ?? ''}'),
                _buildLabel('Payment Status: ${statuses.elementAtOrNull(model.paymentStatus ?? 0) ?? ''}'),
                _buildLabel('Payment Method: ${methods.elementAtOrNull(model.paymentMethod ?? 0)  ?? ''}'),
                _buildLabel('Currency: ${model.currency ?? ''}'),
                _buildLabel('Date: ${model.date ?? ''}'),
                _buildLabel('Location: ${model.location ?? ''}'),
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
