package com.company.jobportal.config;

import com.company.jobportal.entity.job.EmploymentType;
import com.company.jobportal.entity.job.Job;
import com.company.jobportal.entity.job.JobStatus;
import com.company.jobportal.entity.user.RecruiterProfile;
import com.company.jobportal.entity.user.Role;
import com.company.jobportal.entity.user.User;
import com.company.jobportal.repository.job.JobRepository;
import com.company.jobportal.repository.user.RecruiterProfileRepository;
import com.company.jobportal.repository.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Profile("development")
@ConditionalOnProperty(name = "demo.seed.enabled", havingValue = "true")
public class DevelopmentDemoDataInitializer implements CommandLineRunner {

    private static final String DEMO_EMAIL_DOMAIN = "@demo.jobportal.example";
    private static final String DEMO_PASSWORD = "DemoRecruiterPassword123!";

    private final UserRepository userRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobRepository jobRepository;
    private final PasswordEncoder passwordEncoder;

    public DevelopmentDemoDataInitializer(UserRepository userRepository,
                                           RecruiterProfileRepository recruiterProfileRepository,
                                           JobRepository jobRepository,
                                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.jobRepository = jobRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        int createdJobs = 0;
        for (DemoJob demoJob : demoJobs()) {
            RecruiterProfile recruiterProfile = getOrCreateRecruiter(demoJob.companyName());
            if (jobRepository.findAll().stream()
                    .anyMatch(job -> job.getRecruiterProfile().getId().equals(recruiterProfile.getId())
                            && job.getTitle().equals(demoJob.title()))) {
                continue;
            }

            jobRepository.save(Job.builder()
                    .recruiterProfile(recruiterProfile)
                    .title(demoJob.title())
                    .description(demoJob.description())
                    .location(demoJob.location())
                    .employmentType(demoJob.employmentType())
                    .salaryMin(demoJob.salaryMin())
                    .salaryMax(demoJob.salaryMax())
                    .experienceLevel(demoJob.experienceLevel())
                    .requiredSkills(demoJob.requiredSkills())
                    .status(JobStatus.OPEN)
                    .build());
            createdJobs++;
        }
        System.out.println("Development demo seed complete: " + createdJobs + " new jobs created.");
    }

    private RecruiterProfile getOrCreateRecruiter(String companyName) {
        String email = companyName.toLowerCase()
                .replaceAll("[^a-z0-9]+", ".")
                .replaceAll("(^\\.|\\.$)", "") + DEMO_EMAIL_DOMAIN;
        User user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(User.builder()
                .firstName("Demo")
                .lastName("Recruiter")
                .email(email)
                .password(passwordEncoder.encode(DEMO_PASSWORD))
                .role(Role.RECRUITER)
                .enabled(true)
                .build()));

        if (user.getRole() != Role.RECRUITER) {
            throw new IllegalStateException("Demo recruiter email belongs to a non-recruiter user: " + email);
        }

        return recruiterProfileRepository.findByUser(user).orElseGet(() -> recruiterProfileRepository.save(
                RecruiterProfile.builder()
                        .user(user)
                        .companyName(companyName)
                        .companyWebsite("https://" + email.substring(0, email.indexOf(DEMO_EMAIL_DOMAIN)) + ".example")
                        .location("United States")
                        .companyDescription("A fictional company used for development demo listings.")
                        .build()));
    }

    private List<DemoJob> demoJobs() {
        return List.of(
                new DemoJob("Northstar Labs", "Java Developer", "Build and maintain reliable Java services for a fictional logistics platform.", "Austin, TX", EmploymentType.FULL_TIME, 85000.0, 115000.0, "2-4 years", "Java, SQL, Git, REST APIs"),
                new DemoJob("Northstar Labs", "Senior Java Developer", "Lead backend delivery for distributed commerce services and mentor engineers.", "Austin, TX", EmploymentType.FULL_TIME, 120000.0, 155000.0, "5+ years", "Java, Spring, PostgreSQL, Kafka"),
                new DemoJob("Blue Orbit Systems", "Spring Boot Developer", "Create secure Spring Boot APIs that support a growing workflow automation product.", "Denver, CO", EmploymentType.FULL_TIME, 95000.0, 130000.0, "3-5 years", "Spring Boot, Java, JPA, REST, Docker"),
                new DemoJob("Blue Orbit Systems", "Python Developer", "Develop Python services and integrations for operational analytics products.", "Denver, CO", EmploymentType.CONTRACT, 80000.0, 110000.0, "2-4 years", "Python, FastAPI, PostgreSQL, pytest"),
                new DemoJob("Cedar Peak AI", "AI/ML Engineer", "Productionize machine learning workflows for forecasting and intelligent recommendations.", "Seattle, WA", EmploymentType.FULL_TIME, 125000.0, 170000.0, "3-6 years", "Python, PyTorch, MLflow, SQL"),
                new DemoJob("Cedar Peak AI", "Generative AI Engineer", "Prototype and deploy retrieval-augmented generation features for enterprise users.", "Seattle, WA", EmploymentType.FULL_TIME, 135000.0, 185000.0, "4+ years", "Python, LLMs, RAG, vector databases"),
                new DemoJob("Cedar Peak AI", "Data Scientist", "Turn product and customer data into experiments, models, and measurable decisions.", "Seattle, WA", EmploymentType.FULL_TIME, 105000.0, 145000.0, "2-5 years", "Python, pandas, statistics, scikit-learn"),
                new DemoJob("Harbor Metric", "Data Analyst", "Build trusted reporting and answer business questions with clear analysis.", "Chicago, IL", EmploymentType.FULL_TIME, 70000.0, 95000.0, "1-3 years", "SQL, Tableau, Excel, Python"),
                new DemoJob("Pixel Prairie", "React Developer", "Deliver accessible, responsive interfaces for a fictional customer collaboration suite.", "Remote - United States", EmploymentType.FULL_TIME, 90000.0, 125000.0, "2-5 years", "React, JavaScript, TypeScript, CSS"),
                new DemoJob("Pixel Prairie", "Angular Developer", "Build maintainable enterprise dashboards with reusable Angular components.", "Remote - United States", EmploymentType.CONTRACT, 85000.0, 120000.0, "3-5 years", "Angular, TypeScript, RxJS, HTML"),
                new DemoJob("Pixel Prairie", "Full Stack Developer", "Own features from user interface through API and relational data model.", "Remote - United States", EmploymentType.FULL_TIME, 100000.0, 140000.0, "3-6 years", "React, Node.js, Java, PostgreSQL"),
                new DemoJob("Granite Current", "DevOps Engineer", "Improve delivery pipelines, observability, and platform reliability for product teams.", "Boston, MA", EmploymentType.FULL_TIME, 105000.0, 145000.0, "3-6 years", "Docker, Kubernetes, CI/CD, Terraform"),
                new DemoJob("Granite Current", "AWS Cloud Engineer", "Design secure AWS infrastructure and automate repeatable cloud operations.", "Boston, MA", EmploymentType.FULL_TIME, 110000.0, 155000.0, "4+ years", "AWS, Terraform, IAM, networking"),
                new DemoJob("Granite Current", "Cloud Engineer", "Support cloud migration projects and build resilient infrastructure services.", "Boston, MA", EmploymentType.CONTRACT, 95000.0, 135000.0, "3-5 years", "AWS, Azure, Linux, scripting"),
                new DemoJob("Silverline Quality", "QA Automation Engineer", "Create automated test coverage for APIs, web workflows, and release confidence.", "Raleigh, NC", EmploymentType.FULL_TIME, 80000.0, 115000.0, "2-5 years", "Selenium, Java, REST Assured, CI/CD"),
                new DemoJob("Silverline Quality", "Software Engineer", "Implement well-tested product features across a collaborative engineering team.", "Raleigh, NC", EmploymentType.FULL_TIME, 90000.0, 125000.0, "2-5 years", "Java, Python, REST, Git"),
                new DemoJob("Maple Forge", "Backend Developer", "Build scalable services and data integrations for a fictional marketplace.", "Toronto, Canada", EmploymentType.FULL_TIME, 85000.0, 120000.0, "2-4 years", "Java, Spring Boot, MySQL, Redis"),
                new DemoJob("Maple Forge", "Frontend Developer", "Craft polished browser experiences with strong accessibility and performance.", "Toronto, Canada", EmploymentType.FULL_TIME, 80000.0, 115000.0, "2-4 years", "JavaScript, React, HTML, CSS"),
                new DemoJob("Cobalt Grove", "Machine Learning Engineer", "Train, evaluate, and operate models that improve product personalization.", "New York, NY", EmploymentType.FULL_TIME, 120000.0, 165000.0, "3-6 years", "Python, TensorFlow, Kubernetes, MLOps"),
                new DemoJob("Cobalt Grove", "Technical Lead", "Guide architecture and delivery for a small team building dependable SaaS products.", "New York, NY", EmploymentType.FULL_TIME, 130000.0, 175000.0, "7+ years", "Java, cloud architecture, mentoring, system design")
        );
    }

    private record DemoJob(String companyName, String title, String description, String location,
                           EmploymentType employmentType, Double salaryMin, Double salaryMax,
                           String experienceLevel, String requiredSkills) {
    }
}