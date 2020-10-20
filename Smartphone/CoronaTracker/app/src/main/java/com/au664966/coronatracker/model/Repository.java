package com.au664966.coronatracker.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.au664966.coronatracker.database.CountryDAO;
import com.au664966.coronatracker.database.CountryDatabase;

import java.util.List;

/**
 * Implemented Repository pattern so that when we need to add a database or access to the cloud the
 * rest of the app will not be affected, since they will keep using the same functions from the
 * repository.
 * <p>
 * This pattern is recommended by the "Guide to app architecture"
 * https://developer.android.com/jetpack/guide
 */
public class Repository {
    /**
     * Singleton pattern implemented following
     * https://code.tutsplus.com/tutorials/android-design-patterns-the-singleton-pattern--cms-
     * <p>
     * This way we ensure that only one instance of the Repository exists during runtime
     */
    private static Repository instance;
    private LiveData<List<Country>> countries;
    private CountryDAO _countryDao;




    private Repository(Application app) {

        // There's no need to expose the entire database to the repository so we just expose the DAO
        CountryDatabase db = CountryDatabase.getDatabase(app.getApplicationContext());
        _countryDao = db.countryDAO();
        countries = _countryDao.getAll();
    }

    public static Repository getInstance(Application app) {
        if (instance == null) {
            instance = new Repository(app);
        }
        return instance;
    }

    public LiveData<List<Country>> getCountries() {
        return countries;
    }

    /* //TODO: Delete this comment
        private void loadCountries() {
            List<Country> countries = new ArrayList<>();
            countries.add(new Country("Canada", "CA", 142866, 9248));
            countries.add(new Country("China", "CN", 90294, 4736));
            countries.add(new Country("Denmark", "DK", 21836, 635));
            countries.add(new Country("Germany", "DE", 269048, 9376));
            countries.add(new Country("Finland", "FI", 8799, 339));
            countries.add(new Country("India", "IN", 5118253, 83198));
            countries.add(new Country("Japan", "JP", 77488, 1490));
            countries.add(new Country("Norway", "NO", 12644, 266));
            countries.add(new Country("Russian", "RU", 1081152, 18996));
            countries.add(new Country("Sweden", "SE", 87885, 5864));
            countries.add(new Country("USA", "US", 6674411, 197633));
            this.countries.setValue(countries);
        }
    */

    // Room executes all queries on a separate thread, so we don't need to handle queries as
    // async tasks
    public LiveData<Country> getCountryByCode(final String code) {
        return this._countryDao.findCountry(code);
    }

    // Operations like inserting, updating and updating are executed on the same thread so we need
    // To separate them
    public void addCountry(final Country country) {
        CountryDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                _countryDao.addCountry(country);
            }
        });

    }

    public void deleteCountry(final Country country){
        CountryDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                _countryDao.deleteCountry(country);
            }
        });
    }

    public void updateCountry(final Country country){
        CountryDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                _countryDao.updateCountry(country);
            }
        });
    }

    //Threads: https://www.codejava.net/java-core/concurrency/java-concurrency-understanding-thread-pool-and-executors
    // https://medium.com/@frank.tan/using-a-thread-pool-in-android-e3c88f59d07f
}