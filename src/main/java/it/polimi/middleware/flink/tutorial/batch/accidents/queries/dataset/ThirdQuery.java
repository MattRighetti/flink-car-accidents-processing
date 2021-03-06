package it.polimi.middleware.flink.tutorial.batch.accidents.queries.dataset;

import it.polimi.middleware.flink.tutorial.batch.accidents.queries.Query;
import it.polimi.middleware.flink.tutorial.batch.accidents.utils.AccidentField;
import it.polimi.middleware.flink.tutorial.batch.accidents.utils.Functions;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ThirdQuery extends Query {

    public ThirdQuery(ExecutionEnvironment env, String data) {
        super(env, data);
    }

    @Override
    public void execute() throws Exception {

        final String thirdQueryFields = AccidentField.getFields(
                AccidentField.DATE,
                AccidentField.BOROUGH,
                AccidentField.NUMBER_OF_CYCLIST_KILLED,
                AccidentField.NUMBER_OF_MOTORIST_KILLED,
                AccidentField.NUMBER_OF_PEDESTRIANS_KILLED,
                AccidentField.NUMBER_OF_PERSONS_KILLED
        );

        final DataSet<Tuple6<String, String, Integer, Integer, Integer, Integer>> lethalAccidentsDateAndBorough = this.env
                .readCsvFile(this.data)
                .ignoreFirstLine()
                .ignoreInvalidLines()
                .includeFields(thirdQueryFields)
                .types(
                        String.class,
                        String.class,
                        Integer.class,
                        Integer.class,
                        Integer.class,
                        Integer.class
                );

        final DataSet<Tuple4<String, String, Integer, Integer>> boroughNumberOfAccidents = lethalAccidentsDateAndBorough
                .map(tuple -> {
                    int isLethal = (tuple.f2 != 0 || tuple.f3 != 0 || tuple.f4 != 0 || tuple.f5 != 0) ? 1 : 0;
                    return Tuple4.of(tuple.f1, tuple.f0, 1, isLethal);
                })
                .returns(Types.TUPLE(Types.STRING, Types.STRING, Types.INT, Types.INT));

        final DataSet<Tuple5<String, Integer, Integer, Integer, Integer>> boroughNumberOfAccidentsPerWeek = boroughNumberOfAccidents
                .map(tuple -> {
                    Date date = new SimpleDateFormat("dd/MM/yyyy").parse(tuple.f1);
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    int year = calendar.get(Calendar.YEAR);
                    int numberOfWeek = calendar.get(Calendar.WEEK_OF_YEAR);

                    return Tuple5.of(tuple.f0, year, numberOfWeek, tuple.f2, tuple.f3);
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT, Types.INT, Types.INT, Types.INT));

        boroughNumberOfAccidentsPerWeek
                .filter(tuple -> !tuple.f0.isEmpty())
                .groupBy(0, 1, 2)
                .reduce(new Functions.DoubleSum())
                .project(0, 1, 2, 4)
                .print();

        // Average of number of lethal accidents per week
        // grouped by BOROUGH, YEAR
        final DataSet<Tuple3<String, Integer, Float>> averageLethalPerWeekGroupedByYear = boroughNumberOfAccidentsPerWeek
                .filter(tuple -> !tuple.f0.isEmpty())
                .groupBy(0, 1)
                .sum(4)
                .map(tuple -> {
                    float accidentsPerWeekAverage = (float) (((float) tuple.f4) / 52.0);
                    return Tuple3.of(tuple.f0, tuple.f1, accidentsPerWeekAverage);
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT, Types.FLOAT));

        averageLethalPerWeekGroupedByYear.print();

        // Average of number of lethal accidents per week
        // grouped by BOROUGH
        final DataSet<Tuple2<String, Float>> averageLethalPerWeekTotal = averageLethalPerWeekGroupedByYear
                .map(tuple -> Tuple4.of(tuple.f0, tuple.f1, tuple.f2, 1))
                .returns(Types.TUPLE(Types.STRING, Types.INT, Types.FLOAT, Types.INT))
                .groupBy(0)
                .reduce(new Functions.DoubleSumAverage())
                .map(tuple -> {
                    float averageAccidentsPerWeekInYears = tuple.f2 / tuple.f3;
                    return Tuple2.of(tuple.f0, averageAccidentsPerWeekInYears);
                })
                .returns(Types.TUPLE(Types.STRING,Types.FLOAT));

        averageLethalPerWeekTotal.print();

    }

}
