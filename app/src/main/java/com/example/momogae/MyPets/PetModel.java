package com.example.momogae.MyPets;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class PetModel {

    private String uid;
    private String petName;
    private String petAge;
    private String petGender;
    private String petSpecies;
    private String petFirstDate;
    private String petNeutralization;
    private String petAbout;

    public PetModel() {
        // Default constructor required for calls to DataSnapshot.getValue(PetModel.class)
    }

    public PetModel(String uid, String petName, String petAge, String petGender, String petSpecies,
                    String petFirstDate, String petNeutralization, String petAbout) {
        this.uid = uid;
        this.petName = petName;
        this.petAge = petAge;
        this.petGender = petGender;
        this.petSpecies = petSpecies;
        this.petFirstDate = petFirstDate;
        this.petNeutralization = petNeutralization;
        this.petAbout = petAbout;
    }

    public String getUid() {return uid;}
    public String getPetName() {return petName;}
    public String getPetGender() {return petGender;}
    public String getPetSpecies() {return petSpecies;}
    public String getPetAge() {return petAge;}
    public String getPetFirstDate() {return petFirstDate;}
    public String getPetNeutralization() {return petNeutralization;}
    public String getPetAbout() {return petAbout;}

    public void setUid(String uid) {this.uid = uid;}
    public void setpetName(String petName) {this.petName = petName;}
    public void setpetGender(String petGender) {this.petGender = petGender;}
    public void setpetSpecies(String petSpecies) {this.petSpecies = petSpecies;}
    public void setpetAge(String petAge) {this.petAge = petAge;}
    public void setPetFirstDate(String petFirstDate) {this.petFirstDate = petFirstDate;}
    public void setPetNeutralization(String petNeutralization) {this.petNeutralization = petNeutralization;}
    public void setPetAbout(String petAbout) {this.petAbout = petAbout;}

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("petName", petName);
        result.put("petAge", petAge);
        result.put("petGender", petGender);
        result.put("petSpecies", petSpecies);
        result.put("petFirstDate", petFirstDate);
        result.put("petAbout", petAbout);
        result.put("petNeutralization", petNeutralization);
        return result;
    }

}