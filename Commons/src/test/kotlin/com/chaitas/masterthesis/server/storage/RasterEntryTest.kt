//package com.chaitas.masterthesis.server.storage
//
//import com.chaitas.masterthesis.commons.spatial.Geofence
//import com.chaitas.masterthesis.commons.spatial.Location
//import com.chaitas.masterthesis.commons.spatial.RasterEntry
//import org.apache.commons.lang3.tuple.ImmutablePair
//
//import org.junit.After
//import org.junit.Assert.*
//import org.junit.Before
//import org.junit.Test
//import java.util.*
//import java.util.concurrent.*
//import java.util.concurrent.atomic.AtomicInteger
//
//
//private const val THREADS = 10
//private const val OPERATIONS_PER_CLIENT = 10000
//
//class RasterEntryTest {
//
//    private lateinit var executorService: ExecutorService
//
//
//    @Before
//    fun setUp() {
//        executorService = Executors.newFixedThreadPool(THREADS)
//    }
//
//    @After
//    fun tearDown() {
//        executorService.shutdownNow()
//        assertTrue(executorService.isShutdown)
//    }
//
//    /*****************************************************************
//     * Functionality
//     ****************************************************************/
//
//    @Test
//    fun testSubscribeUnsubscribe() {
//        val rasterEntry = RasterEntry(Location.random(), 1.0)
//        val subscriptionId = ImmutablePair("client", UUID.randomUUID().toString().replace("-", ""))
//        rasterEntry.putSubscriptionId(subscriptionId)
//        assertEquals(1, rasterEntry.numberOfSubscriptionIds.toLong())
//        assertEquals(1, rasterEntry.allSubscriptionIds.size.toLong())
//        rasterEntry.removeSubscriptionId(subscriptionId)
//        assertEquals(0, rasterEntry.numberOfSubscriptionIds.toLong())
//        // assertTrue(rasterEntry.allSubscriptionIds.get(subscriptionId.left).isNullOrEmpty())
//
//        // fine, as the HashSet is empty, even though the key persists
//        assertEquals(1, rasterEntry.allSubscriptionIds.size.toLong())
//        // see, it is empty
//        assertEquals(0, rasterEntry.getSubscriptionIdsForClientIdentifier(subscriptionId.left).size.toLong())
//    }
//
//    /*****************************************************************
//     * Immutability (obsolete with Kotlin, as Kotlin Maps are immutable per default
//     ****************************************************************/
//
////    @Test(expected = UnsupportedOperationException::class)
////    @Throws(InterruptedException::class)
////    fun testGetAllImmutability() {
////        val rasterEntry = RasterEntry(Location.random(), 1.0)
////
////        for (i in 0 until THREADS) {
////            val clientIdentifier = System.nanoTime().toString() + ""
////            // every client has its own client identifier and ids are not synchronized
////            executorService!!.submit(FakeClientCallable(clientIdentifier,
////                    OPERATIONS_PER_CLIENT,
////                    rasterEntry,
////                    AtomicInteger(0)))
////        }
////
////        executorService!!.shutdown()
////        executorService!!.awaitTermination(10, TimeUnit.SECONDS)
////
////        // small speed test
////        for (i in 0 until 100 * OPERATIONS_PER_CLIENT) {
////            val size = rasterEntry.allSubscriptionIds.size
////        }
////
////        rasterEntry.allSubscriptionIds.put("fail",
////                Stream.of(ImmutablePair.of("fail",
////                        1)).collect<Set<ImmutablePair<String, String>>, Any>(Collectors.toSet()))
////    }
//
//    /*****************************************************************
//     * RasterEntryBox
//     ****************************************************************/
//
//    @Test
//    fun testRasterEntryBox() {
//        val entry = RasterEntry(Location(1.5, 1.2), 1.0)
//        val expectedBox = Geofence.polygon(Arrays.asList(Location(1.5, 1.2),
//                Location(2.5, 1.2),
//                Location(2.5, 2.2),
//                Location(1.5, 2.2)))
//        assertEquals(expectedBox, entry.rasterEntryBox)
//    }
//
//    /*****************************************************************
//     * Threading
//     ****************************************************************/
//
//    @Test
//    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
//    fun testSingleThreaded() {
//        val rasterEntry = RasterEntry(Location.random(), 1.0)
//        val clientIdentifier = "U3"
//        val f = executorService.submit(FakeClientCallable(clientIdentifier,
//                OPERATIONS_PER_CLIENT,
//                rasterEntry,
//                AtomicInteger(0)))
//        val resultList = f.get(3, TimeUnit.SECONDS)
//
//        // test size
//        assertEquals(resultList.size.toLong(), rasterEntry.numberOfSubscriptionIds.toLong())
//        System.out.println("Raster entry stores ${rasterEntry.numberOfSubscriptionIds} subscriptionIds : ")
//
//        // compare content
//        resultList.removeAll(rasterEntry.getSubscriptionIdsForClientIdentifier(clientIdentifier))
//        assertEquals(0, resultList.size.toLong())
//        System.out.println("SubscriptionsIds of client ${clientIdentifier} match"  )
//    }
//
//    /**
//     * Clients are ALLOWED to add subscriptionIds concurrently for different clientIds without inconsistencies.
//     */
//    @Test
//    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
//    fun testMultiThreadedDifferentClientIds() {
//        val rasterEntry = RasterEntry(Location.random(), 1.0)
//        val futures = HashMap<String, Future<MutableSet<ImmutablePair<String, String>>>>()
//
//        for (i in 0 until THREADS) {
//            val clientIdentifier = System.nanoTime().toString() + ""
//            // every client has its own client identifier and ids are not synchronized
//            futures[clientIdentifier] = executorService.submit(FakeClientCallable(clientIdentifier,
//                    OPERATIONS_PER_CLIENT,
//                    rasterEntry,
//                    AtomicInteger(0)))
//        }
//
//        executorService.shutdown()
//        executorService.awaitTermination(10, TimeUnit.SECONDS)
//
//        var sum = 0
//        for ((clientIdentifier, future) in futures) {
//            // add to sum
//            val idsFromThread = future.get(1, TimeUnit.SECONDS)
//            sum += idsFromThread.size
//
//            // check content
//            val idsFromRaster = rasterEntry.getSubscriptionIdsForClientIdentifier(clientIdentifier)
//            assertEquals(idsFromThread, idsFromRaster)
//            System.out.println("SubscriptionsIds of client ${clientIdentifier} match" )
//        }
//
//        // check size
//        assertEquals(sum.toLong(), rasterEntry.numberOfSubscriptionIds.toLong())
//        System.out.println("Raster entry stored ${rasterEntry.numberOfSubscriptionIds} subscriptionIds" )
//
//    }
//
//    /**
//     * Clients are ALLOWED to add subscriptionIds concurrently for similar clientIds if each subscriptionId is unique
//     * without inconsistencies.
//     */
//    @Test
//    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
//    fun testMultiThreadedSameClientIdSynchronized() {
//        val rasterEntry = RasterEntry(Location.random(), 1.0)
//        val futures = ArrayList<Future<MutableSet<ImmutablePair<String, String>>>>()
//        val atomicInteger = AtomicInteger()
//        val clientIdentifier = "U3"
//
//        for (i in 0 until THREADS) {
//            // every client has its own client identifier and ids are synchronized via an atomic integer
//            futures.add(executorService.submit(FakeClientCallable(clientIdentifier,
//                    OPERATIONS_PER_CLIENT,
//                    rasterEntry,
//                    atomicInteger)))
//        }
//
//        executorService.shutdown()
//        executorService.awaitTermination(10, TimeUnit.SECONDS)
//
//        val idsFromRaster = rasterEntry.getSubscriptionIdsForClientIdentifier(clientIdentifier)
//        val idsFromThreads = HashSet<ImmutablePair<String, String>>()
//
//        var sum = 0
//        for (future in futures) {
//            // add to sum
//            val idsFromThread = future.get(1, TimeUnit.SECONDS)
//            sum += idsFromThread.size
//
//            // add to idsFromThreads
//            idsFromThreads.addAll(idsFromThread)
//        }
//
//        // check size
//        assertEquals(sum.toLong(), rasterEntry.numberOfSubscriptionIds.toLong())
//        System.out.println("Raster entry stored ${rasterEntry.numberOfSubscriptionIds} subscriptionIds")
//
//        // check if all ids in raster have been in threads lists
//        assertEquals(idsFromThreads, idsFromRaster)
//        System.out.println("SubscriptionsIds of client ${clientIdentifier} match" )
//    }
//
//    /**
//     * Clients are NOT ALLOWED to add subscriptionIds concurrently for similar clientIds if subscriptionIds are not
//     * unique -> leads to INCONSISTENCIES
//     */
//    @Test
//    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
//    fun testMultiThreadedSameClientIdNotSynchronized() {
//        val rasterEntry = RasterEntry(Location.random(), 1.0)
//        val futures = ArrayList<Future<MutableSet<ImmutablePair<String, String>>>>()
//        val clientIdentifier = "U3"
//
//        for (i in 0 until THREADS) {
//            // every client has its own client identifier and ids are synchronized via an atomic integer
//            futures.add(executorService.submit(FakeClientCallable(clientIdentifier,
//                    OPERATIONS_PER_CLIENT,
//                    rasterEntry,
//                    AtomicInteger())))
//        }
//
//        executorService.shutdown()
//        executorService.awaitTermination(10, TimeUnit.SECONDS)
//
//        val idsFromRaster = rasterEntry.getSubscriptionIdsForClientIdentifier(clientIdentifier)
//        val idsFromThreads = HashSet<ImmutablePair<String, String>>()
//
//        var sum = 0
//        for (future in futures) {
//            // add to sum
//            val idsFromThread = future.get(1, TimeUnit.SECONDS)
//            sum += idsFromThread.size
//
//            // add to idsFromThreads
//            idsFromThreads.addAll(idsFromThread)
//        }
//
//        System.out.println("Raster entry stored ${ rasterEntry.numberOfSubscriptionIds} subscriptionIds, threads stored ${sum}")
//
//        // check size
//        // assertNotEquals(sum.toLong(), rasterEntry.numberOfSubscriptionIds.toLong())
//
//        // check if all ids in raster have been in threads lists
//        assertNotEquals(idsFromThreads, idsFromRaster)
//        System.out.println("Raster entry has not subscriptionIds that have not been added for client ${clientIdentifier}" )
//    }
//
//}
//
//private class FakeClientCallable(private val clientIdentifier: String, private val numberOfOperations: Int,
//                                 private val rasterEntry: RasterEntry, private val currentId: AtomicInteger) :
//        Callable<MutableSet<ImmutablePair<String, String>>> {
//
//    private val r = Random()
//
//    /**
//     * @return the number of Ids that should be inside the [RasterEntry] for this [FakeClientCallable].
//     */
//    override fun call(): MutableSet<ImmutablePair<String, String>> {
//        val existingIds = ArrayList<ImmutablePair<String, String>>()
//
//        for (i in 0 until numberOfOperations) {
//            if (getTrueWithChance(70)) {
//                val id = UUID.randomUUID().toString().replace("-", "")
//                val subscriptionId = ImmutablePair.of(clientIdentifier, id)
//                rasterEntry.putSubscriptionId(subscriptionId)
//                System.out.println("Added subscriptionId ${subscriptionId}" )
//                existingIds.add(subscriptionId)
//            } else {
//                if (!existingIds.isEmpty()) {
//                    val subscriptionId = existingIds[randomInt(existingIds.size)]
//                    rasterEntry.removeSubscriptionId(subscriptionId)
//                    System.out.println("Removed subscriptionId ${subscriptionId}" )
//                    existingIds.remove(subscriptionId)
//                }
//            }
//        }
//
//        System.out.println("Returning ids: ${existingIds}")
//        return HashSet(existingIds)
//    }
//
//    /**
//     * Returns true with the given chance.
//     *
//     * @param chance - the chance to return true (0 - 100)
//     * @return true, if lucky
//     */
//    fun getTrueWithChance(chance: Int): Boolean {
//        @Suppress("NAME_SHADOWING") var chance = chance
//        // normalize
//        if (chance > 100) {
//            chance = 100
//        } else if (chance < 0) {
//            chance = 0
//        }
//        val random = r.nextInt(100) + 1 // not 0
//        return random <= chance
//    }
//
//    /**
//     * @param bound - int, must be > 0
//     * @return a random int between 0 (inclusive) and bound (exclusive)
//     */
//    fun randomInt(bound: Int): Int {
//        return r.nextInt(bound)
//    }
//}