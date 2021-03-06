package info.appsense.appstore.gradle.plugins.gradle

import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.model.*
import info.appsense.appstore.gradle.plugins.tasks.PublishResourcesTask
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

import static org.mockito.Matchers.*
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.verify
import static org.mockito.MockitoAnnotations.initMocks

/**
 * Created by vladimir.minakov on 17.02.15.
 */
class PublishResourcesTaskTest {
    @Mock
    AndroidPublisher publisher
    @Mock
    AndroidPublisher.Edits edits
    @Mock
    AndroidPublisher.Edits.Insert insert
    @Mock
    AndroidPublisher.Edits.Commit commit
    // AppEdit is final and not mockable
    AppEdit appEdit = new AppEdit().setId("1234567890")

    @Mock
    AndroidPublisher.Edits.Listings listings
    @Mock
    AndroidPublisher.Edits.Listings.List listingsList
    @Mock
    AndroidPublisher.Edits.Listings.Get listingsGet
    @Mock
    AndroidPublisher.Edits.Listings.Update listingsUpdate
    // ListingsListResponse is final and not mockable
    ListingsListResponse listingsListResponse = new ListingsListResponse()
    // Listing is final and not mockable
    Listing listing = new Listing()

    @Mock
    AndroidPublisher.Edits.Tracks tracks
    @Mock
    AndroidPublisher.Edits.Tracks.List tracksList
    // TracksListResponse is final and not mockable
    TracksListResponse tracksListResponse = new TracksListResponse()
    @Mock
    AndroidPublisher.Edits.Tracks.Update tracksUpdate
    // Track is final and not mockable
    Track track = new Track()

    @Mock
    AndroidPublisher.Edits.Apklistings apkListings
    @Mock
    AndroidPublisher.Edits.Apklistings.List apkListingsList
    // ApkListingsListResponse is final and not mockable
    ApkListingsListResponse apkListingsListResponse = new ApkListingsListResponse()

    @Mock
    AndroidPublisher.Edits.Details details
    @Mock
    AndroidPublisher.Edits.Details.Get detailsGet
    // ListingsListResponse is final and not mockable
    AppDetails appDetails = new AppDetails()

    @Mock
    AndroidPublisher.Edits.Apks apks
    @Mock
    AndroidPublisher.Edits.Apks.List apksList
    // ApksListResponse is final and not mockable
    ApksListResponse apksListResponse = new ApksListResponse()
    @Mock
    AndroidPublisher.Edits.Apks.Upload apksUpload
    // Apk is final and not mockable
    Apk apk

    @Mock
    AndroidPublisher.Edits.Images images
    @Mock
    AndroidPublisher.Edits.Images.List imagesList
    // ImagesListResponse is final and not mockable
    ImagesListResponse imagesListResponse = new ImagesListResponse()
    @Mock
    AndroidPublisher.Edits.Images.Upload imagesUpload
    // Image is final and not mockable
    Image image


    @Before
    public void setup() {
        initMocks(this)

        doReturn(edits).when(publisher).edits()
        doReturn(insert).when(edits).insert(anyString(), any(AppEdit.class))
        doReturn(appEdit).when(insert).execute()

        doReturn(commit).when(edits).commit(anyString(), anyString())
        doReturn(appEdit).when(commit).execute()

        doReturn(listings).when(edits).listings()
        doReturn(listingsList).when(listings).list(anyString(), anyString())
        doReturn(listingsListResponse).when(listingsList).execute()
        doReturn(listingsGet).when(listings).get(anyString(), anyString(), anyString())
        doReturn(listing).when(listingsGet).execute()
        doReturn(listingsUpdate).when(listings).update(anyString(), anyString(), anyString(), any(Listing))
        doReturn(listing).when(listingsUpdate).execute()

        doReturn(tracks).when(edits).tracks()
        doReturn(tracksList).when(tracks).list(anyString(), anyString())
        doReturn(tracksListResponse).when(tracksList).execute()
        doReturn(tracksUpdate).when(tracks).update(anyString(), anyString(), anyString(), any(Track))
        doReturn(track).when(tracksUpdate).execute()

        doReturn(apkListings).when(edits).apklistings()
        doReturn(apkListingsList).when(apkListings).list(anyString(), anyString(), anyInt())
        doReturn(apkListingsListResponse).when(apkListingsList).execute()

        doReturn(apks).when(edits).apks()
        doReturn(apksList).when(apks).list(anyString(), anyString())
        doReturn(apksListResponse).when(apksList).execute()
        doReturn(apksUpload).when(apks).upload(anyString(), anyString(), any(FileContent))
        doReturn(apk).when(apksUpload).execute()

        doReturn(images).when(edits).images()
        doReturn(imagesList).when(images).list(anyString(), anyString(), anyString(), anyString())
        doReturn(imagesUpload).when(images).upload(anyString(), anyString(), anyString(), anyString(), any(FileContent))
        doReturn(imagesListResponse).when(imagesList).execute()
        doReturn(image).when(imagesUpload).execute()

        doReturn(details).when(edits).details()
        doReturn(detailsGet).when(details).get(anyString(), anyString())
        doReturn(appDetails).when(detailsGet).execute()

        new File(new File(ProjectFactory.FIXTURES, "store-resources"), "release").mkdirs()
    }

    @After
    public void destroy() {
        new File(ProjectFactory.FIXTURES, "store-resources").delete()
    }

    @Test
    public void testApplicationId() {
        Project project = ProjectFactory.build()
        project.appStoreDeploy {
            googlePlay {
                serviceAccount {
                    email = 'email'
                    storeFile = project.file('key.p12')
                }
            }
        }
        project.evaluate()

        PublishResourcesTask task = project.tasks.publishGooglePlayResourcesRelease as PublishResourcesTask
        task.publisher = publisher
        task.upload()

        verify(edits).insert("com.example", null)
    }
}
