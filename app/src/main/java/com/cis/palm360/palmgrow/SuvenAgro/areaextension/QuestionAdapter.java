package com.cis.palm360.palmgrow.SuvenAgro.areaextension;

import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cis.palm360.palmgrow.SuvenAgro.R;

import java.util.Calendar;
import java.util.List;


public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questions;


    public QuestionAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_item, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.questionText.setText(question.getQuestionText());

        holder.radioGroup.setOnCheckedChangeListener(null); // Reset
        holder.radioGroup.clearCheck();

        holder.radioYes.setEnabled(question.isEnabled());
        holder.radioNo.setEnabled(question.isEnabled());

        // Restore previous selection
        Integer selectedAnswer = question.getSelectedAnswer();

        if (selectedAnswer != null) {
            if (selectedAnswer == 1) {
                holder.radioYes.setChecked(true);
            } else if (selectedAnswer == 0) {
                holder.radioNo.setChecked(true);
            }
        } else {
            // Optional: clear both selections
            holder.radioYes.setChecked(false);
            holder.radioNo.setChecked(false);
        }

        // Show or hide date
        if (question.getSelectedDate() != null && question.isEnabled()) {
            holder.dateText.setVisibility(View.VISIBLE);
            holder.dateText.setText(question.getSelectedDate());
        } else {
            holder.dateText.setVisibility(View.GONE);
        }

        holder.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (!question.isEnabled()) return;

            if (checkedId == R.id.yes_button) {
                showDatePicker(holder.getAdapterPosition(), holder.dateText, position, group);
            } else if (checkedId == R.id.no_button) {
                question.setSelectedAnswer(0);
                question.setSelectedDate(null);
                holder.dateText.setVisibility(View.INVISIBLE);

                for (int i = position + 1; i < questions.size(); i++) {
                    Question nextQuestion = questions.get(i);
                    nextQuestion.setEnabled(false);
                    nextQuestion.setSelectedAnswer(-1);
                    nextQuestion.setSelectedDate(null);
                    notifyItemChanged(i);
                }
            }
        });


    }
    private void showDatePicker(int position, TextView dateTextView, int currentQuestionPosition, RadioGroup radioGroup) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                dateTextView.getContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                    Question currentQuestion = questions.get(currentQuestionPosition);
                    currentQuestion.setSelectedAnswer(1); // ✅ Set answer only after date selected
                    currentQuestion.setSelectedDate(selectedDate);
                    dateTextView.setText(selectedDate);
                    dateTextView.setVisibility(View.VISIBLE);

                    // Enable next question if any
                    if (currentQuestionPosition + 1 < questions.size()) {
                        Question nextQuestion = questions.get(currentQuestionPosition + 1);
                        if (!nextQuestion.isEnabled()) {
                            nextQuestion.setEnabled(true);
                            notifyItemChanged(currentQuestionPosition + 1);
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Handle cancel properly: clear selection
        datePickerDialog.setOnCancelListener(dialog -> {
            if (radioGroup != null) {
                radioGroup.clearCheck(); // Deselect Yes if date not picked
            }
        });

        datePickerDialog.show();
    }


    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;
        RadioGroup radioGroup;
        RadioButton radioYes, radioNo;
        TextView dateText;


        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.question_text);
            radioGroup = itemView.findViewById(R.id.answer_group);
            radioYes = itemView.findViewById(R.id.yes_button);
            radioNo = itemView.findViewById(R.id.no_button);
            dateText = itemView.findViewById(R.id.date_text);
           //  radioGroup = ((RecyclerView.ViewHolder) dateTextView.getTag()).itemView.findViewById(R.id.radioGroup);

        }
    }
}