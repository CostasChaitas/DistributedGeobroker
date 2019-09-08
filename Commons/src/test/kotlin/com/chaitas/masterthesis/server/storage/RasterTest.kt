//package com.chaitas.masterthesis.server.storage
//
//import com.chaitas.masterthesis.commons.spatial.Geofence
//import com.chaitas.masterthesis.commons.spatial.Location
//import com.chaitas.masterthesis.commons.spatial.Raster
//import com.chaitas.masterthesis.commons.spatial.RasterEntry
//import org.apache.commons.lang3.tuple.ImmutablePair
//import org.junit.After
//import org.junit.Assert.*
//import org.junit.Before
//import org.junit.Test
//import org.locationtech.spatial4j.exception.InvalidShapeException
//import java.lang.reflect.InvocationTargetException
//import java.lang.reflect.Method
//import java.util.*
//
//@Suppress("PrivatePropertyName")
//class RasterTest {
//
//    private var privateMethod_calculateIndexLocation: Method? = null
//    private var privateMethod_calculateIndexLocations: Method? = null
//    private var raster: Raster? = null
//
//    @Before
//    @Throws(NoSuchMethodException::class)
//    fun setUpTest() {
//        privateMethod_calculateIndexLocation = Raster::class.java.getDeclaredMethod("calculateIndexLocation",
//                Location::class.java)
//        privateMethod_calculateIndexLocations = Raster::class.java.getDeclaredMethod("calculateIndexLocations",
//                Geofence::class.java)
//
//        privateMethod_calculateIndexLocation!!.isAccessible = true
//        privateMethod_calculateIndexLocations!!.isAccessible = true
//    }
//
//    @After
//    fun tearDownTest() {
//        privateMethod_calculateIndexLocation = null
//        privateMethod_calculateIndexLocations = null
//        raster = null
//    }
//
//    private fun invokeCalculateIndexLocation(location: Location): Location? {
//        try {
//            return privateMethod_calculateIndexLocation!!.invoke(raster, location) as Location
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//            fail("Could not invoke private method")
//        } catch (e: InvocationTargetException) {
//            e.printStackTrace()
//            fail("Could not invoke private method")
//        }
//
//        // Stupid, I never get here, why do I need to return something?
//        return null
//    }
//
//    private fun invokeCalculateIndexLocations(geofence: Geofence): List<RasterEntry>? {
//        try {
//            return privateMethod_calculateIndexLocations!!.invoke(raster, geofence) as List<RasterEntry>
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//            fail("Could not invoke private method")
//        } catch (e: InvocationTargetException) {
//            e.printStackTrace()
//            fail("Could not invoke private method")
//        }
//
//        // Stupid, I never get here, why do I need to return something?
//        return null
//    }
//
//    @Test(expected = InvalidShapeException::class)
//    fun testCalculateIndexGranularity1() {
//        raster = Raster(1)
//        var calculatedIndex = invokeCalculateIndexLocation(Location(10.0, -10.0))
//
//        // even
//        assertEquals(Location(10.0, -10.0), calculatedIndex)
//
//        // many fractions
//        calculatedIndex = invokeCalculateIndexLocation(Location(10.198, -11.198))
//        assertEquals(Location(10.0, -12.0), calculatedIndex)
//
//        // exact boundary
//        calculatedIndex = invokeCalculateIndexLocation(Location(90.0, -180.0))
//        assertEquals(Location(90.0, -180.0), calculatedIndex)
//
//        // out of bounds, expect throw
//        invokeCalculateIndexLocation(Location(91.0, -181.0))
//    }
//
//    @Test(expected = InvalidShapeException::class)
//    fun testCalculateIndexGranularity10() {
//
//        raster = Raster(10)
//        var calculatedIndex = invokeCalculateIndexLocation(Location(10.0, -10.0))
//
//        // even
//        assertEquals(Location(10.0, -10.0), calculatedIndex)
//
//        // many fractions
//        calculatedIndex = invokeCalculateIndexLocation(Location(10.198, -11.198))
//        assertEquals(Location(10.1, -11.2), calculatedIndex)
//
//        // exact boundary
//        calculatedIndex = invokeCalculateIndexLocation(Location(90.0, -180.0))
//        assertEquals(Location(90.0, -180.0), calculatedIndex)
//
//        // out of bounds, expect throw
//        invokeCalculateIndexLocation(Location(91.0, -181.0))
//    }
//
//    @Test(expected = InvalidShapeException::class)
//    fun testCalculateIndexGranularity100() {
//
//        raster = Raster(100)
//        var calculatedIndex = invokeCalculateIndexLocation(Location(10.0, -10.0))
//
//        // even
//        assertEquals(Location(10.0, -10.0), calculatedIndex)
//
//        // many fractions
//        calculatedIndex = invokeCalculateIndexLocation(Location(10.198, -11.198))
//        assertEquals(Location(10.19, -11.2), calculatedIndex)
//
//        // exact boundary
//        calculatedIndex = invokeCalculateIndexLocation(Location(90.0, -180.0))
//        assertEquals(Location(90.0, -180.0), calculatedIndex)
//
//        // out of bounds, expect throw
//        invokeCalculateIndexLocation(Location(91.0, -181.0))
//    }
//
//    @Test
//    fun testCalculateIndexLocationsForGeofenceRectangle() {
//        raster = Raster(1)
//        val fence = Geofence.polygon(Arrays.asList(Location(-0.5, -0.5),
//                Location(-0.5, 1.5),
//                Location(1.5, 1.5),
//                Location(1.5, -0.5)))
//
//        System.out.println(fence)
//
//        val result = invokeCalculateIndexLocations(fence)
//        assertEquals(9, result!!.size.toLong())
//        assertTrue(containsLocation(result, Location(-1.0, -1.0)))
//        assertTrue(containsLocation(result, Location(-1.0, 0.0)))
//        assertTrue(containsLocation(result, Location(-1.0, 1.0)))
//        assertTrue(containsLocation(result, Location(0.0, -1.0)))
//        assertTrue(containsLocation(result, Location(0.0, 0.0)))
//        assertTrue(containsLocation(result, Location(0.0, 1.0)))
//        assertTrue(containsLocation(result, Location(1.0, -1.0)))
//        assertTrue(containsLocation(result, Location(1.0, 0.0)))
//        assertTrue(containsLocation(result, Location(1.0, 1.0)))
//    }
//
//    @Test
//    fun testCalculateIndexLocationsForGeofenceCustomRectangle() {
//        raster = Raster(1)
//        val fence = Geofence("POLYGON((4.7 53.1, 24.7 53.1, 24.7 42.2, 4.7 42.2,4.7 53.1))")
//        val l1 = Location(44.0, 14.4)
//        val l2 = Location(45.0, 9.0)
//        val l3 = Location(48.0, 11.4)
//
//        val result = invokeCalculateIndexLocations(fence)
//        val index1 = invokeCalculateIndexLocation(l1)
//        val index2 = invokeCalculateIndexLocation(l2)
//        val index3 = invokeCalculateIndexLocation(l3)
//
//        System.out.println(result!!.size.toLong())
//
//        assertTrue(containsLocation(result, index1))
//        assertTrue(containsLocation(result, index2))
//        assertTrue(containsLocation(result, index3))
//
//    }
//
//    @Test
//    fun testCalculateIndexLocationsForGeofenceTriangle() {
//        raster = Raster(1)
//        val fence = Geofence.polygon(Arrays.asList(Location(-0.5, -1.5), Location(-0.5, 0.7), Location(1.7, -1.5)))
//
//        val result = invokeCalculateIndexLocations(fence)
//        assertEquals(8, result!!.size.toLong())
//        assertTrue(containsLocation(result, Location(-1.0, -2.0)))
//        assertTrue(containsLocation(result, Location(-1.0, -1.0)))
//        assertTrue(containsLocation(result, Location(-1.0, 0.0)))
//        assertTrue(containsLocation(result, Location(0.0, -2.0)))
//        assertTrue(containsLocation(result, Location(0.0, -1.0)))
//        assertTrue(containsLocation(result, Location(0.0, 0.0)))
//        assertTrue(containsLocation(result, Location(1.0, -2.0)))
//        assertTrue(containsLocation(result, Location(1.0, -1.0)))
//    }
//
//    @Test
//    fun testCalculateIndexLocationsForGeofenceCircle() {
//        raster = Raster(1)
//        val fence = Geofence.circle(Location(0.5, 0.0), 1.1)
//
//        val result = invokeCalculateIndexLocations(fence)
//        assertEquals(8, result!!.size.toLong())
//        assertTrue(containsLocation(result, Location(-1.0, -1.0)))
//        assertTrue(containsLocation(result, Location(-1.0, 0.0)))
//        assertTrue(containsLocation(result, Location(0.0, -2.0)))
//        assertTrue(containsLocation(result, Location(0.0, -1.0)))
//        assertTrue(containsLocation(result, Location(0.0, 0.0)))
//        assertTrue(containsLocation(result, Location(0.0, 1.0)))
//        assertTrue(containsLocation(result, Location(1.0, -1.0)))
//        assertTrue(containsLocation(result, Location(1.0, 0.0)))
//    }
//
//    @Test
//    fun testCalculateIndexLocationsForGeofenceCircle2() {
//        raster = Raster(5)
//        val l = Location(39.984702, 116.318417)
//        val fence = Geofence.circle(l, 0.1)
//
//        val result = invokeCalculateIndexLocations(fence)
//        assertTrue(containsLocation(result!!, Location(39.8, 116.2)))
//        assertTrue(containsLocation(result, Location(39.8, 116.4)))
//        assertTrue(containsLocation(result, Location(40.0, 116.2)))
//        assertTrue(containsLocation(result, Location(40.0, 116.4)))
//        assertEquals(4, result.size.toLong())
//    }
//
//    @Test
//    fun testCalculateIndexLocationsForGeofenceCircle3() {
//        raster = Raster(10)
//        val l = Location(39.984702, 116.318417)
//        val fence = Geofence.circle(l, 0.1)
//
//        val result = invokeCalculateIndexLocations(fence)
//        assertTrue(containsLocation(result!!, Location(39.8, 116.2)))
//        assertTrue(containsLocation(result, Location(39.8, 116.3)))
//        assertTrue(containsLocation(result, Location(39.9, 116.2)))
//        assertTrue(containsLocation(result, Location(39.9, 116.3)))
//        assertTrue(containsLocation(result, Location(39.9, 116.4)))
//        assertTrue(containsLocation(result, Location(40.0, 116.2)))
//        assertTrue(containsLocation(result, Location(40.0, 116.3)))
//        assertTrue(containsLocation(result, Location(40.0, 116.4)))
//        assertEquals(8, result.size.toLong())
//    }
//
//    @Test
//    fun testPutAndThenGetCircle() {
//        raster = Raster(25)
//        val l = Location(40.007499, 116.320013)
//        val fence = Geofence.circle(l, 0.01)
//        val sid = ImmutablePair("test", UUID.randomUUID().toString().replace("-", ""))
//
//        val result = invokeCalculateIndexLocations(fence)
//        val index = invokeCalculateIndexLocation(l)
//        assertTrue(containsLocation(result!!, index))
//
//        raster!!.putSubscriptionIdIntoRasterEntries(fence, sid)
//        val ids = raster!!.getSubscriptionIdsInRasterEntryForPublisherLocation(l)
//        System.out.println(ids)
//    }
//
//    @Test
//    fun testPutAndThenGetPolygon() {
//        raster = Raster(25)
//        val l = Location(44.0, 14.4)
//        val fence = Geofence("POLYGON((4.7 53.1, 24.7 53.1, 24.7 42.2, 4.7 42.2,4.7 53.1))")
//        val sid = ImmutablePair("test", UUID.randomUUID().toString().replace("-", ""))
//
//        val result = invokeCalculateIndexLocations(fence)
//        val index = invokeCalculateIndexLocation(l)
//        assertTrue(containsLocation(result!!, index))
//
//        raster!!.putSubscriptionIdIntoRasterEntries(fence, sid)
//        val ids = raster!!.getSubscriptionIdsInRasterEntryForPublisherLocation(l)
//        System.out.println(ids)
//    }
//
//    private fun containsLocation(result: List<RasterEntry>, l: Location?): Boolean {
//        for (rasterEntry in result) {
//            if (rasterEntry.index == l) {
//                return true
//            }
//        }
//
//        return false
//    }
//
//}