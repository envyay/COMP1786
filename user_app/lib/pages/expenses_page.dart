import 'package:flutter/material.dart';
import 'package:user_app/models/expense_model.dart';
import 'package:user_app/repositories/expense_repo.dart';

import '../widgets/expense_card.dart';
import 'add_expense_page.dart';

class ExpensesPage extends StatefulWidget {
  const ExpensesPage({super.key, required this.projectId});

  final String projectId;

  @override
  State<ExpensesPage> createState() => _ExpensesPageState();
}

class _ExpensesPageState extends State<ExpensesPage> {
  @override
  Widget build(BuildContext context) {
    final expenseRepo = ExpenseRepo();

    return Scaffold(
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          final res = await Navigator.of(context).push<bool>(
            MaterialPageRoute(
              builder: (context) {
                return AddExpensePage(projectId: widget.projectId);
              },
            ),
          );
          if (res is bool && res == true) {
            setState(() {});
          }
        },
        child: Icon(Icons.add),
      ),
      appBar: AppBar(title: const Text('Expenses')),
      body: FutureBuilder<List<ExpenseModel>>(
        future: expenseRepo.getExpensesByProjectId(widget.projectId),
        builder: (context, snapshot) {
          if (snapshot.hasError) {
            return Text(snapshot.error.toString());
          }

          if (snapshot.connectionState == ConnectionState.done) {
            final projects = snapshot.data ?? [];
            return _buildList(expenses: projects);
          }

          return Text('Loading');
        },
      ),
    );
  }

  Widget _buildList({required List<ExpenseModel> expenses}) {
    return ListView.builder(
      itemCount: expenses.length,
      itemBuilder: (context, index) {
        final expense = expenses[index];
        return ExpenseCard(model: expense);
      },
    );
  }
}
