package com.allreplay.magicTv.configuration;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import org.magictvapi.channel.m6replay.service.SfrTvLoger;

import java.util.List;

/**
 * Created by thomas on 09/04/16.
 */
public class KodiGuidedStepFragment extends GuidedStepFragment {

    private static final long INITIAL_DELAY = 10;
    private static final long IDENTIFIANT = 1;
    private static final long PASSWORD = 1;
    private static final long ACTION_CONTINUE = 2;
    private GuidedAction password;
    private GuidedAction identifiant;

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = "Configuration";//getString(R.string.guidedstep_first_title);
        String breadcrumb = "Configuration des chaines SFR"; //getString(R.string.guidedstep_first_breadcrumb);
        String description = "Pour accéder à la chaine ..., il faut posséder un compte SFR / Numéricable. ";//getString(R.string.guidedstep_first_description);
    //    Drawable icon = getActivity().getDrawable(R.drawable.guidedstep_main_icon_1);
        return new GuidanceStylist.Guidance(title, description, breadcrumb, null);
    }

    private GuidedAction addAction(List<GuidedAction> actions,long id, String title, String desc){
        GuidedAction action = new GuidedAction.Builder(this.getActivity())
                .id(id)
                .title(title)
                .description(desc)
                .build();
        actions.add(action);
        return action;
    }

    private GuidedAction addEditableAction(List<GuidedAction> actions,long id, String title, String desc,int inputType){
        GuidedAction action =new GuidedAction.Builder(this.getActivity())
                .id(id)
                .title(title)
                .descriptionEditable(true)
                .descriptionInputType(inputType)
                .description(desc)
                .build();
        actions.add(action);
        return action;
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        identifiant = addEditableAction(actions, IDENTIFIANT, "Identifiant", "", InputType.TYPE_CLASS_TEXT);
        password = addEditableAction(actions, IDENTIFIANT, "Mot de passe", "", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @Override
    public void onCreateButtonActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        super.onCreateButtonActions(actions,savedInstanceState);
        addAction(actions,ACTION_CONTINUE, "Valider","");

    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        super.onGuidedActionClicked(action);
        if (action.getId() == ACTION_CONTINUE) {
            Toast.makeText(getActivity(), "Connexion en cours " ,Toast.LENGTH_LONG).show();
            new AsyncTask<Void, Void, Void>() {
                boolean success = false;

                @Override
                protected Void doInBackground(Void... params) {
                    String token = SfrTvLoger.INSTANCE.logon(identifiant.getDescription().toString(), password.getDescription().toString());
                    success = token != null;
                    Log.d("KodiGuidedStepFragment", "token " + token + " / success ? " + success);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (success) {
                        Toast.makeText(getActivity(), "Connexion ok " ,Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Erreur de login / mot de passe " ,Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();

        }
    }

}
