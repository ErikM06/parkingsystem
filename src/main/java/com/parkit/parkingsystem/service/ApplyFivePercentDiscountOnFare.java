package com.parkit.parkingsystem.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class ApplyFivePercentDiscountOnFare {

	private InputReaderUtil _inputReaderUtil = new InputReaderUtil();
	private ParkingSpotDAO parkingSpotDAO;

	private DataBaseConfig _dataBaseConfig = new DataBaseConfig();

	public void checkRightForFivePercentDiscount(Ticket ticket) {
		Connection con = null;
		ResultSet rs = null;
		String readerRegNumber = null;
		String canGetTheDiscount = null;
		Boolean discount = null;

		try {
			con = _dataBaseConfig.getConnection();

			readerRegNumber = ticket.getVehicleRegNumber();

			PreparedStatement ps = con.prepareStatement(
					"SELECT VEHICLE_REG_NUMBER FROM prod.ticket WHERE VEHICLE_REG_NUMBER = ? AND OUT_TIME IS NOT NULL LIMIT 1");
			ps.setString(1, readerRegNumber);
			rs = ps.executeQuery();
			while (rs.next()) {
				canGetTheDiscount = rs.getString(1);
			}
		} catch (Exception e) {

		}
		if (canGetTheDiscount.equals(readerRegNumber)) {
			discount = true;
			ticket.setGetDiscount(discount);
			System.out.println(
					"Welcome back!  As a recurring user of our parking lot, you'll benefit from a 5% discount.");
		} else {
			discount = false;
			ticket.setGetDiscount(discount);

		}
		_dataBaseConfig.closeConnection(con);
	}

	public void applyFivePercentDiscount(Ticket ticket) {
		double priceWithoutDiscount = 0;
		double priceWithDiscount = 0;
		Boolean applyDiscount;

		applyDiscount = ticket.getDiscount();
		while (applyDiscount.booleanValue()) {
			if (applyDiscount == true) {
				priceWithoutDiscount = ticket.getPrice();
				System.out.println("Price without discount is" + priceWithoutDiscount);
				priceWithDiscount = priceWithoutDiscount * (5 / 100);
				System.out.println("Price with discount is" + priceWithDiscount);
				ticket.setPrice(priceWithDiscount);
			} else {
				ticket.getPrice();
			}
		}

	}
}
