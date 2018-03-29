package com.example.android.project7;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Preference Dialog, customizado para usar um DatePicker para definir a data de inicio e de fim de procura das News
 */

public class DatePreference extends DialogPreference {

    /**
     * Criação das variaveis que serão utilizadas em toda a classe, o picker em si, o String dateto String que ira passar a  informação das preferencias e o CharSequence do sumario
     */
    private DatePicker picker;
    private CharSequence mSummary;

    public DatePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Cria um novo dialogo retornando um date picker
     */
    @Override
    protected View onCreateDialogView() {
        picker = new DatePicker(getContext());

        return picker;
    }

    /**
     * Ao encerrar o dialogo com um ok, define o valor da data no padrão usado no Query do  Guardian News
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            //recebe os valore de dia, mês e ano
            int year = picker.getYear();
            int month = picker.getMonth();
            int day = picker.getDayOfMonth();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            // cria um date format no padrão do query
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateToString = format.format( calendar.getTime() );

            if (callChangeListener( dateToString )) {
                persistString( dateToString );
            }
        }
    }

    public CharSequence getSummary() {
        return mSummary;
    }

    public void setSummary(CharSequence summary) {
        // testa para verificar se existe um sumario e o seta com o valor correto
        if (summary == null && mSummary != null || summary != null
                && !summary.equals(mSummary)) {
            mSummary = summary;
            notifyChanged();
        }
    }
}