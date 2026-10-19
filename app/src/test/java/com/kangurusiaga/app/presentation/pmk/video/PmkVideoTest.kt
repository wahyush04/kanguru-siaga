package com.kangurusiaga.app.presentation.pmk.video

import com.kangurusiaga.app.presentation.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PmkVideoTest {

    @Test
    fun `video data source contains 6 educational modules for bblr pmk`() {
        val videos = PmkVideoDataSource.videos
        assertEquals(6, videos.size)

        // Verify module 1
        assertEquals(1, videos[0].id)
        assertEquals("1. Pengertian PMK", videos[0].title)
        assertEquals("00:27", videos[0].duration)

        // Verify module 2
        assertEquals(2, videos[1].id)
        assertEquals("2. Manfaat PMK untuk Bayi Berat Lahir Rendah", videos[1].title)
        assertEquals("01:05", videos[1].duration)

        // Verify module 3
        assertEquals(3, videos[2].id)
        assertEquals("3. Persiapan sebelum PMK", videos[2].title)
        assertEquals("01:29", videos[2].duration)

        // Verify module 4
        assertEquals(4, videos[3].id)
        assertEquals("4. Cara Perawatan Bayi Berat Lahir Rendah", videos[3].title)
        assertEquals("00:39", videos[3].duration)

        // Verify module 5
        assertEquals(5, videos[4].id)
        assertEquals("5. Tanda Kegawatan Bayi Berat Lahir Rendah", videos[4].title)
        assertEquals("00:54", videos[4].duration)

        // Verify module 6
        assertEquals(6, videos[5].id)
        assertEquals("6. Cara Memandikan Bayi", videos[5].title)
        assertEquals("01:07", videos[5].duration)
    }

    @Test
    fun `each video has valid descriptions and key points`() {
        PmkVideoDataSource.videos.forEach { video ->
            assertTrue("Title should not be blank", video.title.isNotBlank())
            assertTrue("Description should not be blank", video.description.isNotBlank())
            assertTrue("Key points should not be empty", video.keyPoints.isNotEmpty())
            assertTrue("Duration seconds should be > 0", video.durationSeconds > 0)
        }
    }

    @Test
    fun `getVideoById returns corresponding video or fallback`() {
        val video4 = PmkVideoDataSource.getVideoById(4)
        assertEquals(4, video4.id)
        assertEquals("4. Cara Perawatan Bayi Berat Lahir Rendah", video4.title)

        val video1 = PmkVideoDataSource.getVideoById(1)
        assertEquals(1, video1.id)
        assertEquals("1. Pengertian PMK", video1.title)

        // Fallback for non-existent ID
        val fallback = PmkVideoDataSource.getVideoById(999)
        assertNotNull(fallback)
        assertEquals(1, fallback.id)
    }

    @Test
    fun `routes for video list and detail are correctly defined`() {
        assertEquals("pmk_video_list", Screen.PmkVideoList.route)
        assertEquals("pmk_video_detail/{videoId}", Screen.PmkVideoDetail.route)
        assertEquals("pmk_video_detail/4", Screen.PmkVideoDetail.createRoute(4))
    }

    @Test
    fun `video detail fields support stitch layout with clinicalPoints and calloutTip`() {
        PmkVideoDataSource.videos.forEach { video ->
            assertTrue("Badge text should not be blank", video.badgeText.isNotBlank())
            assertTrue("Clinical points should not be empty", video.clinicalPoints.isNotEmpty())
            assertTrue("Callout tip should not be blank", video.calloutTip?.isNotBlank() == true)
            video.clinicalPoints.forEach { point ->
                assertTrue("Point number should be > 0", point.number > 0)
                assertTrue("Point title should not be blank", point.title.isNotBlank())
                assertTrue("Point description should not be blank", point.description.isNotBlank())
            }
        }

        // Verify chaining / nextLessonId
        val video1 = PmkVideoDataSource.getVideoById(1)
        assertEquals(2, video1.nextLessonId)
        val video4 = PmkVideoDataSource.getVideoById(4)
        assertEquals(5, video4.nextLessonId)
        val video5 = PmkVideoDataSource.getVideoById(5)
        assertEquals(null, video5.nextLessonId)
    }
}
