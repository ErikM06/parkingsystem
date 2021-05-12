package com.parkit.parkingsystem;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ApplyFivePercentDiscountOnFare;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
public class ApplyFivePercentDiscountOnFareTest {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static Ticket ticket;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
		dataBasePrepareService.clearDataBaseEntries();

	}

	@BeforeEach
	private void prepareDB() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCD");
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();

	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void checkRightsForFivePercentDiscountTest() {
		Connection con = null;
		ResultSet rs = null;
		String readerRegNumber = null;
		String canGetTheDiscount = null;

		try {
			con = dataBaseTestConfig.getConnection();

			ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			parkingService.processIncomingVehicle();

			readerRegNumber = inputReaderUtil.readVehicleRegistrationNumber();
			PreparedStatement ps = con.prepareStatement(
					"SELECT VEHICLE_REG_NUMBER FROM ticket WHERE VEHICLE_REG_NUMBER = ? AND OUT_TIME IS NOT NULL");
			ps.setString(1, readerRegNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				canGetTheDiscount = rs.getString(1);
			}
			
			assertTrue(canGetTheDiscount.equals(readerRegNumber));
			System.out.println(
					"Welcome back!  As a recurring user of our parking lot, you'll benefit from a 5% discount.");

		} catch (SQLException e) {

		} catch (Exception e) {

		}

	}

	@Test
	public void applyFivePercentDiscountTest() {
		double priceWithDiscount = 0;
		int discount = 0;
		try {
			dataBaseTestConfig.getConnection();
			
			ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			ApplyFivePercentDiscountOnFare applyFivePercentDiscountOnFare = new ApplyFivePercentDiscountOnFare();
			
			ticket.setGetDiscount(discount);
			applyFivePercentDiscountOnFare.applyFivePercentDiscount(ticket);
				
			parkingService.processExitingVehicle();

			assertTrue(priceWithDiscount == ticket.getPrice());

		} catch (SQLException e) {

		} catch (Exception e) {

		}
	}

}
