package com.example.skillnet.Global_Variables;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.skillnet.Models.Categories;
import com.example.skillnet.Models.PersonData;
import com.example.skillnet.Models.Post;
import com.example.skillnet.Models.ReviewModel;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariables extends ViewModel {

  public static boolean isWorker = false;
  public static boolean addAccount = false;
    public static String email = "";
    public static String code = "";
    public static List<Categories> categoriesList = new ArrayList<>();
    public static List<Post> postList = new ArrayList<>();
    public static PersonData otherPersonData = new PersonData();
    public static List<PersonData> personDataList = new ArrayList<>();
    public static List<ReviewModel> reviewModels = new ArrayList<>();
    public static PersonData person = new PersonData();
    public static String otherCode = "";
}
