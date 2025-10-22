/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chriswatnee.martinis.webservice;

import com.chriswatnee.martinis.commandmodel.project.createproject.CreateProjectCommandModel;
import com.chriswatnee.martinis.commandmodel.project.editproject.EditProjectCommandModel;
import com.chriswatnee.martinis.dto.Project;
import com.chriswatnee.martinis.viewmodel.project.createproject.CreateProjectViewModel;
import com.chriswatnee.martinis.viewmodel.project.editproject.EditProjectViewModel;
import com.chriswatnee.martinis.viewmodel.project.projectlist.ProjectListViewModel;
import com.chriswatnee.martinis.viewmodel.project.projectprofile.ProjectProfileViewModel;

/**
 *
 * @author chris
 */
public interface ProjectWebService {
    
    public ProjectListViewModel getProjectListViewModel();
    public ProjectProfileViewModel getProjectProfileViewModel(Integer id);

    public CreateProjectViewModel getCreateProjectViewModel();
    public EditProjectViewModel getEditProjectViewModel(Integer id);

    public Project saveCreateProjectCommandModel(CreateProjectCommandModel createProjectCommandModel);
    public Project saveEditProjectCommandModel(EditProjectCommandModel editProjectCommandModel);

    public Project deleteProject(Integer id);
    
}
