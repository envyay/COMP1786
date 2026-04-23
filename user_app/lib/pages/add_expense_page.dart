import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:user_app/contants/contants.dart';

import '../models/expense_model.dart';
import '../repositories/expense_repo.dart';

class AddExpensePage extends StatefulWidget {
  const AddExpensePage({super.key, required this.projectId});
  final String projectId;

  @override
  State<AddExpensePage> createState() => _AddExpensePageState();
}

class _AddExpensePageState extends State<AddExpensePage> {
  final _formKey = GlobalKey<FormState>();

  final TextEditingController _budgetController = TextEditingController();
  final TextEditingController _claimantController = TextEditingController();
  final TextEditingController _locationController = TextEditingController();
  final TextEditingController _dateController = TextEditingController();

  String? _selectedType;
  String? _selectedStatus;
  String? _selectedMethod;
  DateTime? _selectedDate;

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: DateTime.now(),
      firstDate: DateTime(2000),
      lastDate: DateTime(2101),
    );
    if (picked != null && picked != _selectedDate) {
      setState(() {
        _selectedDate = picked;
        _dateController.text = DateFormat('yyyy-MM-dd').format(picked);
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Add Expense')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              _buildDropdownField(
                'Expense Type',
                expenseTypes,
                _selectedType,
                (val) {
                  setState(() => _selectedType = val);
                },
              ),

              _buildInputField(
                'Budget',
                _budgetController,
                keyboardType: TextInputType.number,
              ),

              _buildInputField('Claimant', _claimantController),

              _buildDropdownField(
                'Payment Status',
                statuses,
                _selectedStatus,
                (val) {
                  setState(() => _selectedStatus = val);
                },
              ),

              _buildDropdownField('Payment Method', methods, _selectedMethod, (
                val,
              ) {
                setState(() => _selectedMethod = val);
              }),

              _buildInputField(
                'Date',
                _dateController,
                isReadOnly: true,
                suffixIcon: Icons.calendar_today,
                onTap: () => _selectDate(context),
              ),

              _buildInputField('Location', _locationController),

              const SizedBox(height: 30),

              ElevatedButton(
                style: ElevatedButton.styleFrom(
                  minimumSize: const Size.fromHeight(50),
                  backgroundColor: Colors.blueAccent,
                  foregroundColor: Colors.white,
                ),
                onPressed: () {
                  if (_formKey.currentState!.validate()) {
                    final expenseRepo = ExpenseRepo();
                    expenseRepo.addExpense(
                      ExpenseModel(
                        projectId: widget.projectId,
                        type: _selectedType,
                        budget: double.parse(_budgetController.text),
                        claimant: _claimantController.text,
                        paymentStatus: statuses.indexOf(_selectedStatus!),
                        paymentMethod: methods.indexOf(_selectedMethod!),
                        date: _selectedDate,
                        location: _locationController.text,
                      ),
                    );

                    Navigator.of(context).pop(true);
                  }
                },
                child: const Text(
                  'SAVE EXPENSE',
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildDropdownField(
      String label,
      List<String> items,
      String? selectedValue,
      Function(String?) onChanged,
      ) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(label, style: const TextStyle(fontWeight: FontWeight.bold)),
          const SizedBox(height: 5),
          DropdownButtonFormField<String>(
            value: selectedValue,
            decoration: _inputDecoration('Select $label'),
            items: items.map((String value) {
              return DropdownMenuItem<String>(value: value, child: Text(value));
            }).toList(),
            onChanged: onChanged,
            validator: (value) => value == null ? 'Please select $label' : null,
          ),
        ],
      ),
    );
  }

  Widget _buildInputField(
      String label,
      TextEditingController controller, {
        TextInputType keyboardType = TextInputType.text,
        bool isReadOnly = false,
        IconData? suffixIcon,
        VoidCallback? onTap,
      }) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(label, style: const TextStyle(fontWeight: FontWeight.bold)),
          const SizedBox(height: 5),
          TextFormField(
            controller: controller,
            keyboardType: keyboardType,
            readOnly: isReadOnly,
            onTap: onTap,
            decoration: _inputDecoration('Enter $label').copyWith(
              suffixIcon: suffixIcon != null ? Icon(suffixIcon) : null,
            ),
            validator: (value) {
              if (label == 'Location') {
                return null;
              }
              if (value == null || value.trim().isEmpty) {
                return 'Please enter $label';
              }
              if (label == 'Budget' && double.tryParse(value) == null) {
                return 'Please enter a valid number';
              }
              return null;
            },
          ),
        ],
      ),
    );
  }

  InputDecoration _inputDecoration(String hint) {
    return InputDecoration(
      hintText: hint,
      filled: true,
      fillColor: Colors.grey[50],
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(10),
        borderSide: BorderSide(color: Colors.grey[300]!),
      ),
      contentPadding: const EdgeInsets.symmetric(horizontal: 15, vertical: 15),
    );
  }
}
