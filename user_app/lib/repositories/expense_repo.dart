import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:user_app/converter/uuid_converter.dart';
import 'package:user_app/models/expense_model.dart';

class ExpenseRepo {
  final _expenses = FirebaseFirestore.instance.collection('expenses');

  Future<List<ExpenseModel>> getExpensesByProjectId(String projectId) async {
    final projectIdJson = uuidToBits(projectId);

    final result = await _expenses
        .where('projectId', isEqualTo: projectIdJson)
        .get();
    return result.docs.map((e) => ExpenseModel.fromJson(e.data())).toList();
  }

  Future<void> addExpense(ExpenseModel expense) async {
    await _expenses.add(expense.toJson());
  }
}
