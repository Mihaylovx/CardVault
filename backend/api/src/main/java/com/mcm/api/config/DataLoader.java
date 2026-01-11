package com.mcm.api.config;

import com.mcm.api.entities.Card;
import com.mcm.api.entities.Listing;
import com.mcm.api.entities.UserAccount;
import com.mcm.api.repository.CardRepository;
import com.mcm.api.repository.ListingRepository;
import com.mcm.api.repository.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner load(CardRepository cards, ListingRepository listings, UserAccountRepository users) {
        return args -> {
            if (cards.count() > 0) return;

            BCryptPasswordEncoder enc = new BCryptPasswordEncoder();

            // UserAccount luffyFan = users.save(new UserAccount("luffyFan", new BigDecimal("250.00")));
            // UserAccount zoroFan = users.save(new UserAccount("zoroFan", new BigDecimal("250.00")));
            //UserAccount namiFan = users.save(new UserAccount("namiFan", enc.encode("password"), new BigDecimal("250.00")));

            //Card c1 = cards.save(new Card("Monkey D. Luffy", "OP-05", "SEC", 2023, null));
            //Card c2 = cards.save(new Card("Roronoa Zoro", "OP-01", "SR", 2022, null));
            //Card c3 = cards.save(new Card("Nami", "OP-01", "SR", 2022, null));
            //Card c4 = cards.save(new Card("Trafalgar Law", "OP-05", "SR", 2023, null));

            //listings.save(new Listing(c1, luffyFan, new BigDecimal("80.00"), 1, "NM"));
            //listings.save(new Listing(c2, zoroFan, new BigDecimal("35.00"), 2, "LP"));
            //listings.save(new Listing(c3, namiFan, new BigDecimal("30.00"), 1, "NM"));
            //listings.save(new Listing(c4, zoroFan, new BigDecimal("20.00"), 3, "NM"));
        };
    }
}
