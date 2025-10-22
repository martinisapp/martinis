/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martinis.webservice;

import com.martinis.commandmodel.project.createproject.CreateProjectCommandModel;
import com.martinis.commandmodel.project.editproject.EditProjectCommandModel;
import com.martinis.dto.Project;
import com.martinis.viewmodel.project.createproject.CreateProjectViewModel;
import com.martinis.viewmodel.project.editproject.EditProjectViewModel;
import com.martinis.viewmodel.project.projectlist.ProjectListViewModel;
import com.martinis.viewmodel.project.projectprofile.ProjectProfileViewModel;

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
