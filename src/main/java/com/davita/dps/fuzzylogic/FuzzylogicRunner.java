package com.davita.dps.fuzzylogic;

import com.intuit.fuzzymatcher.component.MatchService;
import com.intuit.fuzzymatcher.domain.Document;
import com.intuit.fuzzymatcher.domain.Element;
import com.intuit.fuzzymatcher.domain.ElementType;
import com.intuit.fuzzymatcher.domain.Match;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.intuit.fuzzymatcher.domain.ElementType.DATE;

@Component
public class FuzzylogicRunner implements CommandLineRunner {


    @Override
    public void run(String... args) {
        MatchService matchService = new MatchService();

        List<Object> dates = Arrays.asList(getDate("07/01/2019"), getDate("07/03/2019"), getDate("01/01/2020"), getDate("01/02/2020")
                , getDate("01/03/2020"), getDate("02/01/2020"));
        List<Document> documentList1 = getTestDocuments(dates, DATE, 0.9078);
        Map<Document, List<Match<Document>>> result1 = matchService.applyMatch(documentList1);

        result1.forEach((key, value) -> value.forEach(match -> {
            System.out.println("Data: " + match.getData() + " Matched With: " + match.getMatchedWith() + " Score: " + match.getScore().getResult());
        }));
    }

    private List<Document> getTestDocuments(List<Object> values, ElementType elementType, Double neighborhoodRange) {
        AtomicInteger ai = new AtomicInteger(0);
        return values.stream().map(num -> {
            Element.Builder elementBuilder = new Element.Builder().setType(elementType).setValue(num).setThreshold(0.95);
            if (neighborhoodRange != null) {
                elementBuilder.setNeighborhoodRange(neighborhoodRange);
            }
            return new Document.Builder(Integer.toString(ai.incrementAndGet()))
                    .addElement(elementBuilder.createElement()).setThreshold(0.95)
                    .createDocument();
        }).collect(Collectors.toList());
    }

    private Date getDate1(String val) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        try {
            return df.parse(val);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private LocalDate getDate(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");
        return LocalDate.parse(value, formatter);
    }
}
