package org.geekhub.controllers;

import org.geekhub.hibernate.bean.UserBean;
import org.geekhub.hibernate.bean.CourseBean;
import org.geekhub.hibernate.bean.Page;
import org.geekhub.hibernate.entity.Course;
import org.geekhub.hibernate.entity.Role;
import org.geekhub.hibernate.entity.User;
import org.geekhub.hibernate.exceptions.CourseNotFoundException;
import org.geekhub.service.CourseService;
import org.geekhub.service.UserService;
import org.geekhub.util.CommonUtil;
import org.geekhub.util.MailSend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;


@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private MailSend mailSend;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "adminpanel/index";
    }

	@RequestMapping("/users")
	public ModelAndView users(){
		ModelAndView mav=new ModelAndView("adminpanel/users");
		return mav;
	}

//    @RequestMapping("/ajax/usersShow")
//	public ModelAndView usersOnPage(){
//		ModelAndView mav=new ModelAndView("adminpanel/usersShow");
//        List<UserBean> users = userService.getUsersAll();
//		mav.addObject("users",users);
//		return mav;
//	}

    @RequestMapping("/ajax/usersShow")
    public ModelAndView usersOnPage(){
        ModelAndView mav=new ModelAndView("adminpanel/usersShow");
        List<UserBean> users = userService.getUsersOnOnePage();
        mav.addObject("users",users);
        return mav;
    }


//    @RequestMapping(value = "/users", method = RequestMethod.GET)
//    public String users(ModelMap model) {
//        List<User> users = new ArrayList<>();
//        User u = new User();
//        u.setBirthDay(new Date());
//        u.setId(1);
//        u.setFirstName("Test1");
//        u.setEmail("Ivan@mail.ru");
//        u.setIcq("4118377166");
//        u.setLastName("Test");
//        u.setPatronymic("Test");
//        u.setLogin("Ivan123");
//        u.setPassword("1234512");
//        u.setRegistrationDate(new Date());
//        u.setPhoneNumber("+380(93)145-1514");
//        for (int i = 0; i < 5; i++) users.add(u);
//        model.addAttribute("users",users);
//        return "adminpanel/users";
//    }


    @RequestMapping(value = "/users/{userId}/edit", method = RequestMethod.GET)
    public String getEditUserPage(@PathVariable("userId")Integer userId, ModelMap model) throws Exception {
        try {


            Course cour = new Course();
            cour.setId(1);
            cour.setName("PHP");

            Course c2 = new Course();
            c2.setId(2);
            c2.setName("Java Script");

            Set<Course> courses = new HashSet<>();
            courses.add(c2);
            courses.add(cour);

            User u = new User();

            u.setBirthDay(new Date());
            u.setId(userId);
            u.setFirstName("Test1");
            u.setEmail("Ivan@mail.ru");
            u.setIcq("4118377166");
            u.setLastName("Test");
            u.setPatronymic("Test");
            u.setLogin("Ivan123");
            u.setPassword("1234512");
            u.setRegistrationDate(new Date());
          //  u.setCourses(courses);
            u.setPhoneNumber("931451514");

            model.addAttribute("roles", Role.values());
            model.addAttribute("courseList", courses);
            model.addAttribute("user", u);
            return "adminpanel/user-edit";
        }catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String editUser(@RequestParam("id")String id,
                           @RequestParam("login")String login,
                           @RequestParam("first-name")String firstName,
                           @RequestParam("patronymic")String patronymic,
                           @RequestParam("last-name")String lastName,
                           @RequestParam("email")String email,
                           @RequestParam("skype")String skype,
                           @RequestParam("phone")String phone,
                           @RequestParam("birthday")String birthday,
                           @RequestParam("role")String role,
                           @RequestParam("courses[]")String[] courses,
                           @RequestParam(value = "avatar", required = false)MultipartFile avatar,
                           ModelMap model) {
        try {
            Date date = CommonUtil.getFormattedDate(birthday);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "redirect:/dashboard/users/"+id+"/edit";
    }

    @RequestMapping(value = "/courses", method = RequestMethod.GET)
    public String coursesList( @RequestParam(value = "p",required = true,defaultValue = "1")Integer p,
                               @RequestParam(value = "results",defaultValue = "5",required = false) Integer recPerPage,
                               ModelMap modelMap) {

        Page<CourseBean> page = courseService.getAll(p, recPerPage);
        modelMap.addAttribute("page", page);
        return "adminpanel/courses";
    }

    @RequestMapping(value = "/courses/create", method = RequestMethod.GET)
    public String createPage(ModelMap model) {
        model.addAttribute("action", "create");
        model.addAttribute("course", new Course());
        return "adminpanel/course-edit";
    }

    @RequestMapping(value = "/courses/{courseId}/edit", method = RequestMethod.GET)
    public String editCourses(@PathVariable("courseId") Integer courseId, ModelMap model) throws Exception {
        try {
            CourseBean course = courseService.getById(courseId);
            model.addAttribute("course",course);
        }catch (CourseNotFoundException ex){

        }catch (Exception ex) {
            throw new Exception(ex);
        }
        return "adminpanel/course-edit";
    }

    @RequestMapping(value = "/courses/{courseId}", method = RequestMethod.POST)
    public String editCourses(@PathVariable("courseId") Integer courseId,
                              @RequestParam("name") String name, @RequestParam("description") String description) throws Exception {
        try {
            courseService.update(
                    new CourseBean(courseId, name, description)
            );
        }catch (CourseNotFoundException ex){

        }catch (Exception ex) {
            throw new Exception(ex);
        }

        return "redirect:/admin/courses/" + courseId + "/edit";
    }

    @RequestMapping(value = "/courses", method = RequestMethod.POST)
    public String createCourse(@RequestParam("name") String name,
                               @RequestParam("description") String description) throws Exception {

        try {
            courseService.createCourse(name, description);
        } catch (Exception ex) {
            throw new Exception(ex);
        }

        return "redirect:/admin/courses";
    }

    //change method type after activate put methods in project.
    @RequestMapping(value = "/course-remove/{courseId}", method = RequestMethod.GET)
    public String createCourse(@PathVariable("courseId") Integer courseId) throws Exception {

        try {
            courseService.deleteCourse(courseId);
        } catch (Exception ex) {
            throw new Exception(ex);
        }

        return "redirect:/admin/courses";
    }

    @RequestMapping(value = "/profile/{userId}", method = RequestMethod.GET)
    public String profilePage(@PathVariable("userId")Integer userId, ModelMap model) throws Exception {
        try {
            Course cour = new Course();
            cour.setId(1);
            cour.setName("PHP");

            Course c2 = new Course();
            c2.setId(2);
            c2.setName("Java Script");

            Set<Course> courses = new HashSet<>();
            courses.add(c2);
            courses.add(cour);

            User u = new User();

            u.setBirthDay(new Date());
            u.setId(userId);
            u.setFirstName("Test1");
            u.setEmail("Ivan@mail.ru");
            u.setIcq("4118377166");
            u.setLastName("Test");
            u.setPatronymic("Test");
            u.setLogin("Ivan123");
            u.setPassword("1234512");
            u.setRegistrationDate(new Date());
//            u.setCourses(courses);
            u.setPhoneNumber("931451514");
//            u.setRoles(Role.ROLE_ADMIN);
            model.addAttribute("user", u);
            return "adminpanel/user-profile";
        }catch (Exception ex) {
            throw new Exception(ex);
        }
    }


    @RequestMapping(value = "/createFeedback/{userid}", method = RequestMethod.GET)
    public String createFeedback(@PathVariable("userid")Integer userid,HttpServletRequest request){
        String feedback = (String) request.getAttribute("feedback");
        userService.setFeedback(userid,feedback);
        return "redirect:/admin/users/" + userid + "/edit";
    }

}
