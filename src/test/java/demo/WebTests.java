package demo;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class WebTests {

  @Autowired
  WebApplicationContext webContext;

  @Test
  public void homePage_unauthenticatedUser() throws Exception {
    MockMvc mockMvc = MockMvcBuilders
        .webAppContextSetup(webContext)
        .apply(springSecurity())
        .build();

    mockMvc.perform(get("/"))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().string("Location", "http://localhost/login"));
  }
  
  @Test
  @WithUserDetails("craig")
  public void homePage_authenticatedUser() throws Exception {
    MockMvc mockMvc = MockMvcBuilders
        .webAppContextSetup(webContext)
        .apply(springSecurity())
        .build();

    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("readingList"))
        .andExpect(model().attribute("reader", 
                           samePropertyValuesAs(getCraigReader())))
        .andExpect(model().attribute("books", hasSize(0)))
        .andExpect(model().attribute("amazonID", "habuma-20"));
  }

  private Reader getCraigReader() {
    Reader craig = new Reader();
    craig.setUsername("craig");
    craig.setPassword("password");
    craig.setFullname("Craig Walls");
    return craig;
  }

}
