import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ru.yandex.loginapp.LoginScreenState
import ru.yandex.loginapp.LoginViewModel

private const val VALID_EMAIL = "valid_email@example.com"
private const val INVALID_EMAIL = "invalid_email"
private const val VALID_PASSWORD = "password"

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel()
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_with_empty_fields_sets_empty_fields_error() = runTest {
        viewModel.login("", "")
        Assert.assertEquals(LoginScreenState.EmptyFieldsError, viewModel.state.value)
    }

    @Test
    fun login_with_invalid_email_sets_email_validation_error() = runTest {
        viewModel.login(INVALID_EMAIL, VALID_PASSWORD)
        Assert.assertEquals(LoginScreenState.EmailValidationError, viewModel.state.value)
    }

    @Test
    fun login_with_valid_data_sets_loading() = runTest {
        viewModel.login(VALID_EMAIL, VALID_PASSWORD)
        testDispatcher.scheduler.runCurrent()
        Assert.assertEquals(LoginScreenState.Loading, viewModel.state.value)
    }

    @Test
    fun login_with_valid_data_sets_loading_then_success() = runTest {
        viewModel.login(VALID_EMAIL, VALID_PASSWORD)

        testDispatcher.scheduler.runCurrent()
        Assert.assertEquals(LoginScreenState.Loading, viewModel.state.value)

        testDispatcher.scheduler.advanceUntilIdle()
        Assert.assertEquals(LoginScreenState.Success, viewModel.state.value)
    }
}