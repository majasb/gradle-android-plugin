/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jvoegele.gradle.enhancements

import org.gradle.api.Project

class IdeaEnhancement extends GradlePluginEnhancement {

  def androidConvention = project.convention.plugins.android

  public IdeaEnhancement(Project project) {
    super(project)
  }

  public void apply() {
    project.gradle.taskGraph.whenReady { taskGraph ->

      if (!project.plugins.hasPlugin('idea'))
        return;

      addAndroidFacet()
    }
  }

  def addAndroidFacet() {
    project.idea.module {
      iml.withXml { provider ->
        def facetManager = provider.node.find{it.@name=='FacetManager'}
        if (!facetManager) {
          facetManager = provider.node.appendNode('component', ['name': 'FacetManager'])
        }
        def androidFacet = facetManager.find{it.@name=='Android'}
        if (!androidFacet) {
          androidFacet = facetManager.appendNode('facet', [type: 'android', name: 'Android'])
        }
        def configuration = androidFacet.configuration
        if (configuration) {
          configuration = configuration.first()
        } else {
          configuration = androidFacet.appendNode('configuration')
        }
        def libraryProject = configuration.find{it.@name=='LIBRARY_PROJECT'}
        if (!libraryProject) {
          libraryProject = configuration.appendNode('option', ['name': 'LIBRARY_PROJECT'])
        }
        libraryProject.@value = isLibraryProject()
      }
    }
  }

  def isLibraryProject() {
    def isLibrary = project.ant.properties.get('android.library')
    return isLibrary != null && isLibrary;
  }

}
