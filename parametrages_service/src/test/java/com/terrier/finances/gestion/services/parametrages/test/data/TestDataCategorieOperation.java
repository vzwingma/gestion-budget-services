package com.terrier.finances.gestion.services.parametrages.test.data;

import com.terrier.finances.gestion.communs.parametrages.model.v12.CategorieOperation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Jeu de données Catégorie
 */
public class TestDataCategorieOperation {

    public static List<CategorieOperation> getListeTestCategories(){
        List<CategorieOperation> categoriesFromSPI = new ArrayList<>();
        CategorieOperation catAlimentation = new CategorieOperation();
        catAlimentation.setId("8f1614c9-503c-4e7d-8cb5-0c9a9218b84a");
        catAlimentation.setActif(true);
        catAlimentation.setCategorie(true);
        catAlimentation.setLibelle("Alimentation");

        CategorieOperation ssCatCourse = new CategorieOperation();
        ssCatCourse.setActif(true);
        ssCatCourse.setCategorie(false);
        ssCatCourse.setId("467496e4-9059-4b9b-8773-21f230c8c5c6");
        ssCatCourse.setLibelle("Courses");
        ssCatCourse.setListeSSCategories(null);
        catAlimentation.setListeSSCategories(new HashSet<>());
        catAlimentation.getListeSSCategories().add(ssCatCourse);
        categoriesFromSPI.add(catAlimentation);
        return categoriesFromSPI;
    }

}
