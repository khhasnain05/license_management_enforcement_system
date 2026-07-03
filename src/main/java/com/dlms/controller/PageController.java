package com.dlms.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // =========================
    // AUTH PAGES
    // =========================

    @GetMapping("/")
    public String root() {
        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // =========================
    // ROLE BASED DASHBOARD
    // =========================

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {

        if (auth != null && auth.getAuthorities() != null) {

            if (auth.getAuthorities().contains(
                    new SimpleGrantedAuthority("ROLE_LICENSING_OFFICER"))) {
                return "officer-dashboard";
            }

            if (auth.getAuthorities().contains(
                    new SimpleGrantedAuthority("ROLE_TRAFFIC_POLICE"))) {
                return "police-dashboard";
            }

            if (auth.getAuthorities().contains(
                    new SimpleGrantedAuthority("ROLE_TESTING_OFFICER"))) {
                return "testing-dashboard";
            }
        }

        return "applicant-dashboard";
    }

    // =========================
    // TEST PAGES
    // =========================

    @GetMapping("/test")
    public String testPage() {
        return "test-page";
    }

    @GetMapping("/testing-dashboard")
    public String testingDashboard() {
        return "testing-dashboard";
    }

    @GetMapping("/theory-test")
    public String theoryTestPage() {
        return "theory-test"; 
    }

    // =========================
    // APPLICANT PAGES
    // =========================

    @GetMapping("/applicant-dashboard")
    public String applicantDashboard() {
        return "applicant-dashboard";
    }

    // =========================
    // OFFICER PAGES
    // =========================

    @GetMapping("/officer-dashboard")
    public String officerDashboard() {
        return "officer-dashboard";
    }

    // =========================
    // POLICE PAGES
    // =========================

    @GetMapping("/police-dashboard")
    public String policeDashboard() {
        return "police-dashboard";
    }


    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/license-status")
    public String licenseStatusPage() {
        return "license-status"; 
    }

    @GetMapping("/notifications")
    public String notificationsPage() {
        return "notifications"; 
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "profile";
    }
}