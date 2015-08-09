package br.com.rsm.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import br.com.rsm.dao.DirectionDao;
import br.com.rsm.enumeradores.EnumUFId;
import br.com.rsm.model.City;

public class Main {

	public static void main(String[] args) throws IOException {

		// System.out.println("Goiás: "+EnumState.GO);

		// Map<String, Integer> estados = new HashMap<String, Integer>();
		File file = new File(
				"/Users/patricknasc/Desktop/RSM/DB/municipios3.csv");

		FileReader fr = new FileReader(file);
		BufferedReader in = new BufferedReader(fr);
		String line;
		int i = 0;
		String[] stateArray;
		City city;

		DirectionDao directionDao = new DirectionDao();
		while ((line = in.readLine()) != null) {
			stateArray = line.split("\\s*,\\s*");
			System.out.print(stateArray[4].trim());
			System.out.println(" - " + stateArray[1].trim());
			// //System.out.println(" x  3 - "+EnumState.valueOf(stateArray[1].trim()).toString());
			// System.out.println(" x  4 - "+EnumState.valueOf(stateArray[1].trim()).getV());

			// EnumState.valueOf(stateArray[1].trim()).toString()

			city = new City(stateArray[4].trim(), EnumUFId.valueOf(
					stateArray[1].trim()).getV());
			try {
				directionDao.save(city);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// estados.put(line, i++);
		}

	}
}
