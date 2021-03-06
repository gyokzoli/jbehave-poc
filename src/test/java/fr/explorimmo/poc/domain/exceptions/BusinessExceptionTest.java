/**
 * 
 */
package fr.explorimmo.poc.domain.exceptions;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.Assert;

import org.junit.Test;

import fr.explorimmo.poc.domain.Constants;
import fr.explorimmo.poc.domain.exceptions.BusinessException;

/**
 * @author louis.gueye@gmail.com
 */
public class BusinessExceptionTest {

	/**
	 * Test method for
	 * {@link fr.explorimmo.poc.domain.exceptions.BusinessException#getMessage()} .
	 */
	@Test
	public final void testGetMessage() {
		final String messageCode = "test.code";
		final Object[] messageArgs = new Object[] { "sdfsdf", 5L };
		final String defaultMessage = "default message";
		final String preferredLanguage = Locale.ENGLISH.getLanguage();
		final Throwable cause = new Throwable("bla bla");
		BusinessException e = null;

		e = new BusinessException(null, messageArgs, defaultMessage, cause);
		Assert.assertEquals(e.getDefaultMessage(), e.getMessage(preferredLanguage));

		final String rawMessage = ResourceBundle.getBundle(Constants.MESSAGES_BUNDLE_NAME).getString(messageCode);
		e = new BusinessException(messageCode, null, defaultMessage, cause);
		Assert.assertEquals(rawMessage, e.getMessage(preferredLanguage));

		e = new BusinessException(messageCode, messageArgs, defaultMessage, cause);
		Assert.assertEquals(MessageFormat.format(rawMessage, messageArgs), e.getMessage(preferredLanguage));
	}

}
